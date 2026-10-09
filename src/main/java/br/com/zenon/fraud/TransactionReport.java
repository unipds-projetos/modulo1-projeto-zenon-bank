package br.com.zenon.fraud;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.stream.Stream;

import br.com.zenon.fraud.security.MethodGuard;

public record TransactionReport(long lineCount, long fraudCount, BigDecimal totalAmount) {

    private static final int MAX_LINE_LENGTH = 512;
    private static final String HEADER = "step,type,amount,nameOrig,oldbalanceOrg,newbalanceOrig,"
            + "nameDest,oldbalanceDest,newbalanceDest,isFraud,isFlaggedFraud";

    public TransactionReport {
        if (lineCount < 0 || fraudCount < 0 || fraudCount > lineCount) {
            throw new IllegalArgumentException("report counts are invalid");
        }
        if (totalAmount == null || totalAmount.signum() < 0) {
            throw new IllegalArgumentException("totalAmount is invalid");
        }
    }

    public static TransactionReport read(String fileName) throws IOException {
        Path source = MethodGuard.dataFile(fileName);
        long lines = 0;
        long frauds = 0;
        BigDecimal total = BigDecimal.ZERO;

        try (Stream<String> rows = Files.lines(source, StandardCharsets.UTF_8)) {
            Iterator<String> iterator = rows.iterator();
            if (!iterator.hasNext() || !HEADER.equals(iterator.next())) {
                throw new IllegalArgumentException("csv header is not the PaySim schema");
            }

            while (iterator.hasNext()) {
                String line = iterator.next();
                if (line.isBlank()) {
                    continue;
                }
                if (line.length() > MAX_LINE_LENGTH) {
                    throw new IllegalArgumentException(MethodGuard.rejectedRow(line));
                }
                Contribution contribution = parse(line);
                lines++;
                total = total.add(contribution.amount());
                if (contribution.fraud()) {
                    frauds++;
                }
            }
        }

        return new TransactionReport(lines, frauds, total);
    }

    private static Contribution parse(String line) {
        try {
            String[] columns = line.split(",", -1);
            if (columns.length != 11) {
                throw new IllegalArgumentException("csv row should have 11 colunas");
            }
            BigDecimal amount = MethodGuard.money(new BigDecimal(columns[2].trim()), "amount");
            boolean fraud = MethodGuard.fraudFlag(columns[9].trim());
            return new Contribution(amount, fraud);
        } catch (RuntimeException error) {
            throw new IllegalArgumentException(MethodGuard.rejectedRow(line));
        }
    }

    private record Contribution(BigDecimal amount, boolean fraud) {
    }
}
