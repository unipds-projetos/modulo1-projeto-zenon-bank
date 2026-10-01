package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

public class TransactionReport {
    private record ReportTransaction(BigDecimal amount, boolean isFraud){

    }

    public record Statistics(long totalTransactions, long totalFrauds, BigDecimal totalAmount){
        private final static Statistics ZERO = new Statistics(0,0, BigDecimal.ZERO);

        private Statistics addReportTransaction(ReportTransaction reportTransaction){
            return new Statistics(
                    totalTransactions + 1,
                    totalFrauds + (reportTransaction.isFraud ? 1 : 0),
                    totalAmount.add(reportTransaction.amount));
        }

        private Statistics add(Statistics anotherStatistics){
            return new Statistics(totalTransactions + anotherStatistics.totalTransactions,
                    totalFrauds + anotherStatistics.totalFrauds,
                    totalAmount.add(anotherStatistics.totalAmount));
        }
    }

    public Statistics generateReport(String fileName){
        Path path = Path.of(fileName);
        try (Stream<String> lines = Files.lines(path)) {
            return lines
                    .skip(1)
                    .map(this::parseReportTransaction)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .reduce(
                            Statistics.ZERO,
                            Statistics::addReportTransaction,
                            Statistics::add
                    );

        } catch (Exception e) {
            throw new RuntimeException("< Error >: ", e);
        }
    }

private Optional<ReportTransaction> parseReportTransaction(String line) {
    try {
        String[] chunks = line.split(",");
        if (chunks[2] == null || chunks[2].trim().isEmpty())
            throw new IllegalArgumentException("The value of 'amount' cannot be null");

        BigDecimal amount = new BigDecimal(chunks[2]);

        boolean isFraud = "1".equals(chunks[9]);

        return Optional.of(new ReportTransaction(amount, isFraud));

    } catch (Exception e) {
        System.err.println("Error at parse: " + line + " | " + e);
        return Optional.empty();
    }
    }
}