package br.com.zenon.fraud;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException {
        String fileName = "data/PS_20174392719_1491204439457_log.csv";
        FraudAnalyzer analyzer = new FraudAnalyzer(new TransactionIngestor().ingest(fileName));

        System.out.println("1. Total de Fraudes: " + analyzer.fraudCount());

        System.out.println("2. Top 3 Fraudes de Maior Valor:");
        for (BigDecimal amount : analyzer.largestFraudAmounts(3)) {
            System.out.println(money(amount));
        }

        System.out.println("3. Clientes Suspeitos:");
        for (String customer : analyzer.largestSuspiciousCustomers(5)) {
            System.out.println(customer);
        }

        System.out.println("4. Prejuízo Total: " + money(analyzer.totalLoss()));

        System.out.println("5. Fraudes por Tipo:");
        analyzer.fraudsByType().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> System.out.println("- " + entry.getKey() + ": " + entry.getValue()));
    }

    private static String money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
