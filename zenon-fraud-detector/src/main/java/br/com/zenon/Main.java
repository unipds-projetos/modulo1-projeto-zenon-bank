package br.com.zenon;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class Main {

    void main() {

        var t1 = new Transaction(1, TransactionType.PAYMENT, new BigDecimal("9838.64"), new TransactionCustumer("C1231006815",
                new BigDecimal("170136.0"),new BigDecimal("160296.36")), new TransactionCustumer("M1979787155", new BigDecimal("0.0"), new BigDecimal("0.0")),
                false, false);

        var t2 = new Transaction(743, TransactionType.CASH_OUT, new BigDecimal("850002.52"), new TransactionCustumer("C1280323807",
                new BigDecimal("850002.52"),new BigDecimal("0.0")), new TransactionCustumer("C873221189",
                new BigDecimal("6510099.11"), new BigDecimal("7360101.63")), true, false);

        IO.println(t1);
        IO.println(t2);

        IO.println("------------------------------------");

        var transactionIngestor = new TransactionIngestor();
        List<Transaction> transactions = transactionIngestor.read("data/PS_20174392719_1491204439457_log.csv");
        IO.println(transactions.size());

        transactions.stream().limit(10).forEach(IO::println);

        IO.println("------------------------------------");

        List<Transaction> transactionsBadData = transactionIngestor.read("data/paysim_with_bad_data.csv");
        IO.println(transactionsBadData.size());

        transactionsBadData.forEach(IO::println);

        IO.println("------------------------------------");

        var fraudAnalyzer = new FraudAnalyzer(transactions);

        //Apenas transações onde isFraud == true, imprima o tamanho da lista
        long fraudCount = fraudAnalyzer.countFrauds();
        IO.println("Total de fraudes: " + fraudCount);

        IO.println("------------------------------------");
        //imprima as 3 fraudes de maior valor(amount)
        List<BigDecimal> highestFraudsAmounts = fraudAnalyzer.findHighestValueFraudsAmounts(3);
        IO.println("Top 3 fraudes com maior valor: ");
        highestFraudsAmounts.forEach(amout -> IO.println(amout.toBigInteger() + " Doletas"));

        IO.println("------------------------------------");

        /*Obter apenas os nomes dos clientes de origem (nameOrig) dessas fraudes e depois gere uma
        lista sem repetições (Set ou distinct) com os 5 maiores clientes suspeitos.*/
        List<String> supiciousClients = fraudAnalyzer.findTopSuspiciousClients(5);
        IO.println("Top 5 clientes suspeitos: ");
        supiciousClients.forEach(IO::println);

        IO.println("------------------------------------");

        //Calcule o prejuízo total causado pelas fraudes (soma dos amount).

        BigDecimal totalFraudLoss = fraudAnalyzer.calculateTotalFraudLoss();
        IO.println("Prejuizo total: " + totalFraudLoss + " Doletas");

        IO.println("------------------------------------");

        //Conte quantas fraudes ocorreram por tipo de transação (CASH_OUT, TRANSFER, etc...).

        Map<TransactionType, Long> fraudCountByType = fraudAnalyzer.countFraudsByType();
        IO.println("Fraudes por tipo:");
        fraudCountByType.forEach((type, count) -> IO.println("-" + type + ": " + count));

    }
}
