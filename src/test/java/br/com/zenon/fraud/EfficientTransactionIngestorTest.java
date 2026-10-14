package br.com.zenon.fraud;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.BufferedWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EfficientTransactionIngestorTest {

    private static final String HEADER = "step,type,amount,nameOrig,oldbalanceOrg,newbalanceOrig,"
            + "nameDest,oldbalanceDest,newbalanceDest,isFraud,isFlaggedFraud";

    @Test
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void streamsTenThousandRowsAndBatchesTheWholeFile() throws Exception {
        try (DataCsv csv = DataCsv.allocate()) {
            try (BufferedWriter writer = Files.newBufferedWriter(csv.file())) {
                writer.write(HEADER);
                writer.newLine();
                for (int index = 0; index <= EfficientTransactionIngestor.STREAM_LIMIT; index++) {
                    writer.write(row("C" + index, "1.00", "0"));
                    writer.newLine();
                }
            }

            try (EfficientTransactionIngestor ingestor = EfficientTransactionIngestor.platformThreads(1)) {
                AtomicInteger streamed = new AtomicInteger();
                AtomicBoolean sawTheRowPastTheLimit = new AtomicBoolean();
                ingestor.readAsStream(csv.relativePath(), transaction -> {
                    streamed.incrementAndGet();
                    if (("C" + EfficientTransactionIngestor.STREAM_LIMIT).equals(transaction.origin().name())) {
                        sawTheRowPastTheLimit.set(true);
                    }
                });
                assertEquals(EfficientTransactionIngestor.STREAM_LIMIT, streamed.get());
                assertFalse(sawTheRowPastTheLimit.get());

                ConcurrentLinkedQueue<Integer> sizes = new ConcurrentLinkedQueue<>();
                AtomicInteger batched = new AtomicInteger();
                ingestor.readBatch(csv.relativePath(), batch -> {
                    assertThrows(UnsupportedOperationException.class, () -> batch.add(batch.getFirst()));
                    sizes.add(batch.size());
                    batched.addAndGet(batch.size());
                });
                assertEquals(EfficientTransactionIngestor.STREAM_LIMIT + 1, batched.get());
                assertEquals(Set.of(EfficientTransactionIngestor.BATCH_SIZE, 1), Set.copyOf(sizes));
            }
        }
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void processesBatchesOnSeparateThreads() throws Exception {
        try (DataCsv csv = rows("C1", "C2", "C3", "C4");
                EfficientTransactionIngestor ingestor = new EfficientTransactionIngestor(
                        Executors.newFixedThreadPool(2), 2, 2)) {
            CyclicBarrier barrier = new CyclicBarrier(2);
            AtomicInteger batches = new AtomicInteger();
            ingestor.readBatch(csv.relativePath(), batch -> {
                assertEquals(2, batch.size());
                if (batches.incrementAndGet() <= 2) {
                    try {
                        barrier.await(2, TimeUnit.SECONDS);
                    } catch (Exception error) {
                        throw new IllegalStateException("batches did not run together", error);
                    }
                }
            });
            assertEquals(2, batches.get());
        }
    }

    @Test
    void keepsValidRowsWhenOneLineIsRejected() throws Exception {
        try (DataCsv csv = DataCsv.write(HEADER + "\n" + row("C1", "1.00", "0") + "\n"
                + "1,PAYMENT,-1.00,C2,1.00,0.00,M1,0.00,0.00,0,0\n"
                + row("C3", "1.0E7", "1") + "\n");
                EfficientTransactionIngestor ingestor = EfficientTransactionIngestor.platformThreads(1)) {
            List<Transaction> accepted = new ArrayList<>();
            try (StderrCapture stderr = new StderrCapture()) {
                ingestor.readBatch(csv.relativePath(), accepted::addAll);
                assertTrue(stderr.text().contains("linha rejeitada"));
                assertFalse(stderr.text().contains("-1.00"));
                assertFalse(stderr.text().contains("C2"));
            }
            assertEquals(List.of("C1", "C3"), accepted.stream().map(item -> item.origin().name()).toList());
            assertEquals(new BigDecimal("10000000"), accepted.getLast().amount());
            assertTrue(accepted.getLast().isFraud());
        }
    }

    @Test
    void rejectsPathsArgumentsAndThreadCountsBeforeReading() throws Exception {
        try (EfficientTransactionIngestor ingestor = EfficientTransactionIngestor.platformThreads(1)) {
            assertEquals(
                    "consumer",
                    assertThrows(NullPointerException.class, () -> ingestor.readAsStream("data/qualquer.csv", null))
                            .getMessage());
            assertEquals(
                    "consumer",
                    assertThrows(NullPointerException.class, () -> ingestor.readBatch("data/qualquer.csv", null))
                            .getMessage());
            assertEquals(
                    "transaction limit is outside the allowed range",
                    assertThrows(IllegalArgumentException.class,
                            () -> ingestor.readBatch("data/../pom.xml", batch -> { }, 0))
                            .getMessage());
            assertEquals(
                    "fileName must stay inside data/",
                    assertThrows(IllegalArgumentException.class,
                            () -> ingestor.readBatch("data/../pom.xml", batch -> { }))
                            .getMessage());
        }

        assertEquals(
                "thread count is outside the allowed range",
                assertThrows(IllegalArgumentException.class, () -> EfficientTransactionIngestor.platformThreads(0))
                        .getMessage());
        assertEquals(
                "thread count is outside the allowed range",
                assertThrows(IllegalArgumentException.class, () -> EfficientTransactionIngestor.virtualThreads(17))
                        .getMessage());

        ExecutorService executor = Executors.newFixedThreadPool(1);
        try {
            assertEquals(
                    "executor",
                    assertThrows(NullPointerException.class, () -> new EfficientTransactionIngestor(null, 1, 1))
                            .getMessage());
            assertEquals(
                    "batch size is outside the allowed range",
                    assertThrows(IllegalArgumentException.class, () -> new EfficientTransactionIngestor(executor, 1, 0))
                            .getMessage());
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void reportsTheConsumerFailureAfterTheRunningBatchesFinish() throws Exception {
        try (DataCsv csv = rows("C1", "C2");
                EfficientTransactionIngestor ingestor = EfficientTransactionIngestor.platformThreads(1)) {
            IllegalStateException error = assertThrows(IllegalStateException.class,
                    () -> ingestor.readBatch(csv.relativePath(), batch -> {
                        throw new IllegalStateException("batch failed");
                    }));
            assertEquals("batch failed", error.getMessage());
        }
    }

    @Test
    void rejectsImpossibleTotals() {
        assertEquals(
                "totals are invalid",
                assertThrows(IllegalArgumentException.class, () -> new DatasetTotals(-1, 0, BigDecimal.ZERO))
                        .getMessage());
        assertEquals(
                "totalAmount is invalid",
                assertThrows(IllegalArgumentException.class, () -> new DatasetTotals(1, 0, null)).getMessage());
    }

    private static DataCsv rows(String... origins) throws Exception {
        StringBuilder csv = new StringBuilder(HEADER);
        for (String origin : origins) {
            csv.append('\n').append(row(origin, "1.00", "0"));
        }
        csv.append('\n');
        return DataCsv.write(csv.toString());
    }

    private static String row(String origin, String amount, String fraud) {
        return "1,PAYMENT," + amount + "," + origin + ",1.00,0.00,M1,0.00,0.00," + fraud + ",0";
    }
}
