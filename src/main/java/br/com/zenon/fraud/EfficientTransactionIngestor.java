package br.com.zenon.fraud;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import br.com.zenon.fraud.security.MethodGuard;

public final class EfficientTransactionIngestor implements AutoCloseable {

    static final int BATCH_SIZE = 10_000;
    static final int STREAM_LIMIT = 10_000;
    private static final int MAX_PARALLELISM = 16;
    private static final int MAX_LINE_LENGTH = 512;
    private static final int READ_BUFFER = 64 * 1024;
    private static final String HEADER = "step,type,amount,nameOrig,oldbalanceOrg,newbalanceOrig,"
            + "nameDest,oldbalanceDest,newbalanceDest,isFraud,isFlaggedFraud";

    private final ExecutorService executor;
    private final int maxInFlight;
    private final int batchSize;

    public static EfficientTransactionIngestor platformThreads(int threads) {
        int count = parallelism(threads);
        return new EfficientTransactionIngestor(platformPool(count), count, BATCH_SIZE);
    }

    public static EfficientTransactionIngestor virtualThreads(int maxInFlight) {
        int count = parallelism(maxInFlight);
        return new EfficientTransactionIngestor(virtualPool(), count, BATCH_SIZE);
    }

    EfficientTransactionIngestor(ExecutorService executor, int maxInFlight, int batchSize) {
        this.executor = MethodGuard.notNull(executor, "executor");
        this.maxInFlight = parallelism(maxInFlight);
        if (batchSize < 1 || batchSize > BATCH_SIZE) {
            throw new IllegalArgumentException("batch size is outside the allowed range");
        }
        this.batchSize = batchSize;
    }

    public void readAsStream(String fileName, Consumer<Transaction> consumer) throws IOException {
        read(fileName, STREAM_LIMIT, () -> false, MethodGuard.notNull(consumer, "consumer"));
    }

    public void readBatch(String fileName, Consumer<List<Transaction>> consumer) throws IOException {
        readBatch(fileName, MethodGuard.notNull(consumer, "consumer"), Long.MAX_VALUE);
    }

    void readBatch(String fileName, Consumer<List<Transaction>> consumer, long maxTransactions) throws IOException {
        if (maxTransactions < 1) {
            throw new IllegalArgumentException("transaction limit is outside the allowed range");
        }
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Semaphore slots = new Semaphore(maxInFlight);
        ArrayList<Transaction> batch = new ArrayList<>(batchSize);
        try {
            read(fileName, maxTransactions, () -> failure.get() != null, transaction -> {
                batch.add(transaction);
                if (batch.size() == batchSize) {
                    dispatch(consumer, List.copyOf(batch), slots, failure);
                    batch.clear();
                }
            });
            if (failure.get() == null && !batch.isEmpty()) {
                dispatch(consumer, List.copyOf(batch), slots, failure);
            }
        } finally {
            awaitIdle(slots);
        }
        Throwable error = failure.get();
        if (error instanceof RuntimeException runtime) {
            throw runtime;
        }
        if (error instanceof Error serious) {
            throw serious;
        }
        if (error != null) {
            throw new IllegalStateException("ingestion failed", error);
        }
    }

    @Override
    public void close() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException error) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void dispatch(
            Consumer<List<Transaction>> consumer,
            List<Transaction> rows,
            Semaphore slots,
            AtomicReference<Throwable> failure) {
        try {
            slots.acquire();
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("ingestion was interrupted", error);
        }
        try {
            executor.execute(() -> {
                try {
                    consumer.accept(rows);
                } catch (Throwable error) {
                    failure.compareAndSet(null, error);
                } finally {
                    slots.release();
                }
            });
        } catch (RuntimeException error) {
            slots.release();
            throw error;
        }
    }

    private void awaitIdle(Semaphore slots) {
        try {
            slots.acquire(maxInFlight);
        } catch (InterruptedException error) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            throw new IllegalStateException("ingestion was interrupted", error);
        }
    }

    private static void read(
            String fileName,
            long maxTransactions,
            BooleanSupplier stop,
            Consumer<Transaction> consumer) throws IOException {
        Path source = MethodGuard.dataFile(fileName);
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Files.newInputStream(source), StandardCharsets.UTF_8),
                READ_BUFFER)) {
            String header = reader.readLine();
            if (!HEADER.equals(header)) {
                throw new IllegalArgumentException("csv header is not the PaySim schema");
            }

            long accepted = 0;
            String line;
            while (accepted < maxTransactions && !stop.getAsBoolean() && (line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                if (line.length() > MAX_LINE_LENGTH) {
                    System.err.println("Erro: " + MethodGuard.rejectedRow(line));
                    continue;
                }
                Transaction transaction;
                try {
                    transaction = parse(line);
                } catch (IllegalArgumentException error) {
                    System.err.println("Erro: " + MethodGuard.rejectedRow(line) + " | " + error.getClass().getSimpleName());
                    continue;
                }
                consumer.accept(transaction);
                accepted++;
            }
        }
    }

    private static Transaction parse(String line) {
        String[] columns = line.split(",", -1);
        if (columns.length != 11) {
            throw new IllegalArgumentException("csv row should have 11 colunas");
        }
        return new Transaction(
                Integer.parseInt(columns[0].trim()),
                TransactionType.valueOf(columns[1].trim().replace('-', '_')),
                new BigDecimal(columns[2].trim()),
                new TransactionCustomer(
                        columns[3].trim(), new BigDecimal(columns[4].trim()), new BigDecimal(columns[5].trim())),
                new TransactionCustomer(
                        columns[6].trim(), new BigDecimal(columns[7].trim()), new BigDecimal(columns[8].trim())),
                MethodGuard.fraudFlag(columns[9].trim()),
                MethodGuard.fraudFlag(columns[10].trim()));
    }

    private static int parallelism(int threads) {
        if (threads < 1 || threads > MAX_PARALLELISM) {
            throw new IllegalArgumentException("thread count is outside the allowed range");
        }
        return threads;
    }

    private static ExecutorService platformPool(int count) {
        return Executors.newFixedThreadPool(count, Thread.ofPlatform().name("zenon-ingest-", 0).factory());
    }

    private static ExecutorService virtualPool() {
        return Executors.newThreadPerTaskExecutor(Thread.ofVirtual().name("zenon-ingest-", 0).factory());
    }
}
