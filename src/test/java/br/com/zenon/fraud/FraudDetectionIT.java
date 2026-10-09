package br.com.zenon.fraud;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FraudDetectionIT {

    @Test
    void ingestsTheSampleAndAgreesAcrossAnalysisAndBothRepositories() throws Exception {
        try (DataCsv csv = copiedSample()) {
            ingestsTheSample(csv);
        }
    }

    private void ingestsTheSample(DataCsv csv) throws Exception {
        List<Transaction> transactions;
        try (StderrCapture stderr = new StderrCapture()) {
            transactions = new TransactionIngestor().ingest(csv.relativePath());
            String errors = stderr.text();
            assertEquals(9, errors.lines().filter(line -> line.startsWith("Erro: ")).count());
            assertTrue(errors.contains("linha rejeitada"));
            assertFalse(errors.contains("-1,PAYMENT,1000.00,C10001"));
            assertFalse(errors.contains("1,COMPRA_INVALIDA,100.00,C10006"));
            assertFalse(errors.contains("1,PAYMENT,10.00,C10009"));
        }

        assertEquals(9, transactions.size());
        assertEquals(new Transaction(
                1,
                TransactionType.PAYMENT,
                new BigDecimal("5000.00"),
                new TransactionCustomer("C1000001", new BigDecimal("5000.00"), new BigDecimal("0.00")),
                new TransactionCustomer("M1000001", new BigDecimal("0.00"), new BigDecimal("0.00")),
                false,
                false), find(transactions, "C1000001"));

        Transaction cashOut = find(transactions, "C1000003");
        assertEquals(TransactionType.CASH_OUT, cashOut.type());
        assertEquals(new BigDecimal("10.00"), cashOut.amount());

        Transaction flaggedOnly = find(transactions, "C1000004");
        assertFalse(flaggedOnly.isFraud());
        assertTrue(flaggedOnly.isFlaggedFraud());
        assertEquals(new BigDecimal("300000.00"), flaggedOnly.amount());

        FraudAnalyzer analyzer = new FraudAnalyzer(transactions);
        assertEquals(3, analyzer.fraudCount());
        assertEquals(new BigDecimal("1450.00"), analyzer.totalLoss());
        assertEquals(
                List.of(new BigDecimal("1200.00"), new BigDecimal("200.00"), new BigDecimal("50.00")),
                analyzer.largestFraudAmounts(10));
        assertEquals(List.of("C1000008", "C1231006815", "C1000002"), analyzer.largestSuspiciousCustomers(10));
        assertEquals(Map.of(TransactionType.PAYMENT, 1L, TransactionType.TRANSFER, 2L), analyzer.fraudsByType());

        TransactionRepository listRepository = new TransactionListRepository(transactions);
        TransactionRepository mapRepository = new TransactionMapRepository(transactions);
        assertEquals(listRepository.findByOriginName("C1231006815"), mapRepository.findByOriginName("C1231006815"));
        assertEquals(listRepository.findByOriginName("C1000002"), mapRepository.findByOriginName("C1000002"));
        assertTrue(listRepository.findByOriginName("C12345").isEmpty());
        assertTrue(mapRepository.findByOriginName("C12345").isEmpty());
        assertEquals(new BigDecimal("200.00"), listRepository.findByOriginName("C1231006815").orElseThrow().amount());
    }

    @Test
    void printsTheCliReportForTheSameSample() throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        try (DataCsv csv = copiedSample();
                StderrCapture ignored = new StderrCapture()) {
            Main.run(csv.relativePath(), out);
        }

        String output = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Transação não encontrada para o cliente C12345"));
        assertTrue(output.contains("C1231006815"));
        assertTrue(output.contains("type=TRANSFER"));
        assertTrue(output.contains("amount=200.00"));
        assertTrue(output.lines().anyMatch(line -> line.matches("Busca na lista por C1000002: \\d+ ns")));
        assertTrue(output.lines().anyMatch(line -> line.matches("Busca no mapa por C1000002: \\d+ ns")));
    }

    @Test
    void cliRejectsAnEmptyFileBeforePrintingTheReport() throws IOException {
        try (DataCsv empty = DataCsv.write("")) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            IllegalArgumentException error = assertThrows(
                    IllegalArgumentException.class,
                    () -> Main.run(empty.relativePath(), new PrintStream(buffer, true, StandardCharsets.UTF_8)));

            assertEquals("csv header is not the PaySim schema", error.getMessage());
            assertEquals("", buffer.toString(StandardCharsets.UTF_8));
        }
    }

    @Test
    void cliReportsTheFixedCustomersWhenTheHeaderHasNoRows() throws IOException {
        try (DataCsv headerOnly = DataCsv.write(
                "step,type,amount,nameOrig,oldbalanceOrg,newbalanceOrig,nameDest,oldbalanceDest,newbalanceDest,isFraud,isFlaggedFraud\n")) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            assertThrows(
                    NoSuchElementException.class,
                    () -> Main.run(headerOnly.relativePath(), new PrintStream(buffer, true, StandardCharsets.UTF_8)));

            String output = buffer.toString(StandardCharsets.UTF_8);
            assertTrue(output.contains("Transação não encontrada para o cliente C12345"));
            assertTrue(output.contains("Transação não encontrada para o cliente C1231006815"));
            assertFalse(output.contains("Busca na lista"));
        }
    }

    private static Transaction find(List<Transaction> transactions, String origin) {
        return transactions.stream()
                .filter(transaction -> transaction.origin().name().equals(origin))
                .findFirst()
                .orElseThrow();
    }

    private static DataCsv copiedSample() throws Exception {
        Path source = Path.of(FraudDetectionIT.class.getResource("/paysim/mixed.csv").toURI());
        return DataCsv.write(Files.readString(source));
    }
}
