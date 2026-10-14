package br.com.zenon.fraud;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionIngestorTest {

    private static final String HEADER = "step,type,amount,nameOrig,oldbalanceOrg,newbalanceOrig,"
            + "nameDest,oldbalanceDest,newbalanceDest,isFraud,isFlaggedFraud";

    private final TransactionIngestor ingestor = new TransactionIngestor();

    @Test
    void readsAValidPaySimRowAfterTheHeader() throws IOException {
        try (DataCsv csv = DataCsv.write(HEADER + """

                1,CASH-OUT,10.50,C100,1000.00,989.50,C200,0.00,10.50,1,0
                """)) {
            List<Transaction> transactions;
            try (StderrCapture stderr = new StderrCapture()) {
                transactions = ingestor.ingest(csv.relativePath());
                assertEquals("", stderr.text());
            }

            assertEquals(List.of(new Transaction(
                    1,
                    TransactionType.CASH_OUT,
                    new BigDecimal("10.50"),
                    Fixtures.customer("C100", "1000.00", "989.50"),
                    Fixtures.customer("C200", "0.00", "10.50"),
                    true,
                    false)), transactions);
            assertThrows(UnsupportedOperationException.class, () -> transactions.add(transactions.getFirst()));
        }
    }

    @Test
    void trimsColumnsAndAcceptsOnlyZeroOrOneAsFlags() throws IOException {
        try (DataCsv csv = DataCsv.write(HEADER + """

                1, PAYMENT, 1.00, C1, 1.00, 0.00, M1, 0.00, 0.00, 0, 1
                1,PAYMENT,1.00,C2,1.00,0.00,M1,0.00,0.00,1 ,0
                1,PAYMENT,1.00,C3,1.00,0.00,M1,0.00,0.00,2,0
                """)) {
            List<Transaction> transactions;
            try (StderrCapture stderr = new StderrCapture()) {
                transactions = ingestor.ingest(csv.relativePath());
                assertFalse(stderr.text().contains("C3"));
                assertTrue(stderr.text().contains("linha rejeitada"));
            }

            assertEquals(2, transactions.size());
            assertFalse(transactions.get(0).isFraud());
            assertTrue(transactions.get(0).isFlaggedFraud());
            assertEquals(TransactionType.PAYMENT, transactions.get(0).type());
            assertTrue(transactions.get(1).isFraud());
            assertFalse(transactions.get(1).isFlaggedFraud());
        }
    }

    @Test
    void skipsBlankLinesWithoutConsumingTheRowBudget() throws IOException {
        try (DataCsv csv = DataCsv.write(HEADER + "\n\n   \n" + row("C1") + "\n")) {
            assertEquals(1, ingestQuietly(csv).size());
        }
    }

    @Test
    void skipsInvalidRowsAndDoesNotEchoThem() throws IOException {
        try (DataCsv csv = DataCsv.write(HEADER + """

                -1,PAYMENT,10.00,C1,10.00,0.00,M1,0.00,0.00,0,0
                1,PAYMENT,-5.00,C2,10.00,5.00,M2,0.00,0.00,0,0
                1,NOPE,5.00,C3,10.00,5.00,M3,0.00,0.00,0,0
                1,PAYMENT,5.00,,10.00,5.00,M4,0.00,0.00,0,0
                1,PAYMENT,,C5,10.00,0.00,M5,0.00,0.00,0,0
                1,PAYMENT,5.00,C6,10.00,5.00,M6,0.00,0.00
                2,CASH_IN,8.00,C7,0.00,8.00,C8,8.00,0.00,0,0
                """)) {
            List<Transaction> transactions;
            try (StderrCapture stderr = new StderrCapture()) {
                transactions = ingestor.ingest(csv.relativePath());
                String errors = stderr.text();
                assertEquals(6, errors.lines().filter(line -> line.startsWith("Erro: ")).count());
                assertTrue(errors.contains("linha rejeitada"));
                assertTrue(errors.contains("IllegalArgumentException"));
                assertTrue(errors.contains("NumberFormatException"));
                assertFalse(errors.contains("step should be positive"));
                assertFalse(errors.contains("-5.00"));
                assertFalse(errors.contains("NOPE"));
                assertFalse(errors.contains("11 colunas"));
                assertFalse(errors.contains("-1,PAYMENT,10.00,C1"));
                assertFalse(errors.contains("1,PAYMENT,5.00,C6"));
            }

            assertEquals(1, transactions.size());
            assertEquals("C7", transactions.getFirst().origin().name());
            assertEquals(TransactionType.CASH_IN, transactions.getFirst().type());
        }
    }

    @Test
    void rejectsALineThatExceedsFiveHundredAndTwelveCharacters() throws IOException {
        String oversized = row("C1") + "x".repeat(500);
        try (DataCsv csv = DataCsv.write(HEADER + "\n" + oversized + "\n" + row("C2") + "\n")) {
            List<Transaction> transactions;
            try (StderrCapture stderr = new StderrCapture()) {
                transactions = ingestor.ingest(csv.relativePath());
                String errors = stderr.text();
                assertTrue(errors.contains("linha rejeitada (" + oversized.length() + " caracteres)"));
                assertFalse(errors.contains("IllegalArgumentException"));
                assertFalse(errors.contains(oversized));
            }

            assertEquals(List.of("C2"), transactions.stream().map(transaction -> transaction.origin().name()).toList());
        }
    }

    @Test
    void requiresThePaySimHeader() throws IOException {
        try (DataCsv empty = DataCsv.write("");
                DataCsv wrong = DataCsv.write("step,type\n");
                DataCsv headerOnly = DataCsv.write(HEADER + "\n")) {
            assertEquals(
                    "csv header is not the PaySim schema",
                    assertThrows(IllegalArgumentException.class, () -> ingestor.ingest(empty.relativePath())).getMessage());
            assertEquals(
                    "csv header is not the PaySim schema",
                    assertThrows(IllegalArgumentException.class, () -> ingestor.ingest(wrong.relativePath())).getMessage());
            assertEquals(List.of(), ingestQuietly(headerOnly));
        }
    }

    @Test
    void acceptsOnlyCsvFilesInsideTheDataDirectory() throws IOException {
        Files.createDirectories(Path.of("data"));
        Path missing = Path.of("data", "zz-test-ausente-" + UUID.randomUUID() + ".csv");

        NoSuchFileException notFound = assertThrows(
                NoSuchFileException.class, () -> ingestor.ingest("data/" + missing.getFileName()));
        assertEquals(missing.getFileName().toString(), notFound.getMessage());

        assertEquals(
                "fileName is invalid",
                assertThrows(IllegalArgumentException.class, () -> ingestor.ingest(" ")).getMessage());
        assertEquals(
                "fileName must stay inside data/",
                assertThrows(IllegalArgumentException.class, () -> ingestor.ingest("/tmp/fora.csv")).getMessage());
        assertEquals(
                "fileName must stay inside data/",
                assertThrows(IllegalArgumentException.class, () -> ingestor.ingest("data/../pom.xml")).getMessage());

        Path text = Path.of("data", "zz-test-" + UUID.randomUUID() + ".txt");
        Files.writeString(text, HEADER);
        try {
            assertEquals(
                    "fileName must end with .csv",
                    assertThrows(IllegalArgumentException.class,
                            () -> ingestor.ingest("data/" + text.getFileName())).getMessage());
        } finally {
            Files.deleteIfExists(text);
        }
    }

    @Test
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void stopsAfterOneHundredThousandNonBlankRowsAndCountsInvalidOnes() throws IOException {
        Path csv = Path.of("data", "zz-test-limite-" + UUID.randomUUID() + ".csv");
        Files.createDirectories(csv.getParent());
        try {
            try (BufferedWriter writer = Files.newBufferedWriter(csv)) {
                writer.write(HEADER);
                writer.newLine();
                writer.write("invalid");
                writer.newLine();
                for (int i = 0; i < 100_000; i++) {
                    writer.write(row("C" + i));
                    writer.newLine();
                }
                writer.write(row("C999999"));
                writer.newLine();
            }

            List<Transaction> transactions;
            try (StderrCapture stderr = new StderrCapture()) {
                transactions = ingestor.ingest("data/" + csv.getFileName());
                assertTrue(stderr.text().contains("linha rejeitada"));
                assertFalse(stderr.text().contains("invalid"));
            }

            assertEquals(99_999, transactions.size());
            assertEquals("C0", transactions.getFirst().origin().name());
            assertEquals("C99998", transactions.getLast().origin().name());
        } finally {
            Files.deleteIfExists(csv);
        }
    }

    @Test
    void acceptsTheScientificNotationUsedByPaySim() throws IOException {
        try (DataCsv csv = DataCsv.write(HEADER + """

                1,TRANSFER,1.0E7,C7162498,1.0E7,0.00,C945327594,0.00,0.00,1,0
                """)) {
            List<Transaction> transactions = ingestQuietly(csv);

            assertEquals(1, transactions.size());
            assertEquals(new BigDecimal("10000000"), transactions.getFirst().amount());
            assertEquals(new BigDecimal("10000000"), transactions.getFirst().origin().oldBalance());
            assertTrue(transactions.getFirst().isFraud());
        }
    }

    @Test
    void stopsAtTheRequestedLineLimit() throws IOException {
        try (DataCsv csv = DataCsv.write(HEADER + "\n" + row("C1") + "\n" + row("C2") + "\n" + row("C3") + "\n")) {
            List<Transaction> transactions;
            try (StderrCapture stderr = new StderrCapture()) {
                transactions = ingestor.ingest(csv.relativePath(), 2);
                assertEquals("", stderr.text());
            }

            assertEquals(2, transactions.size());
            assertEquals("C1", transactions.getFirst().origin().name());
            assertEquals("C2", transactions.getLast().origin().name());
        }
    }

    @Test
    void rejectsALineLimitOutsideTheIngestorCeiling() {
        assertEquals(
                "line limit is outside the allowed range",
                assertThrows(IllegalArgumentException.class, () -> ingestor.ingest("data/../pom.xml", 0)).getMessage());
        assertEquals(
                "line limit is outside the allowed range",
                assertThrows(IllegalArgumentException.class, () -> ingestor.ingest("data/qualquer.csv", 100_001)).getMessage());
    }

    private List<Transaction> ingestQuietly(DataCsv csv) throws IOException {
        try (StderrCapture stderr = new StderrCapture()) {
            List<Transaction> transactions = ingestor.ingest(csv.relativePath());
            assertEquals("", stderr.text());
            return transactions;
        }
    }

    private static String row(String origin) {
        return "1,PAYMENT,1.00," + origin + ",1.00,0.00,M1,0.00,0.00,0,0";
    }
}
