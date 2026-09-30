package br.com.zenon.fraud;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Main{
    void main(){
        var transaction1 = new Transaction(1, TransactionType.PAYMENT, new BigDecimal("9838.64"),
        new TransactionCustomer("C1231006815", new BigDecimal("170136.0"), new BigDecimal("160296.36")),
        new TransactionCustomer("M1979787155", new BigDecimal("0.0"), new BigDecimal("0.0")),
        false, false
        );

        var transaction2 = new Transaction(743, TransactionType.CASH_OUT, new BigDecimal("850002.52"),
        new TransactionCustomer("C1280323807", new BigDecimal("850002.52"), new BigDecimal("0.0")),
        new TransactionCustomer("C873221189", new BigDecimal("6510099.11"), new BigDecimal("7360101.63")),
        true, false
        );

        IO.println(transaction1);
        IO.println(transaction2);
        IO.println("--------------");
        var transactionIngestor = new TransactionIngestor();
        List<Transaction> transactions = transactionIngestor.read("data/PS_20174392719_1491204439457_log.csv");

        transactions.stream().limit(10).forEach(IO::println);
        //IO.println(transactions.size());

        IO.println("--------------");
        List<Transaction> transactionsBadData = transactionIngestor.read("data/paysim_with_bad_data.csv");
        IO.println(transactionsBadData.size());
        transactionsBadData.stream().forEach(IO::println);

        IO.println("--------------");
        var fraudAnalyzer = new FraudAnalyzer(transactions);
        var fraudCounts = fraudAnalyzer.countFrauds();
        IO.println("1. Total de fraudes: " + fraudCounts);

        List<BigDecimal> topFraudsAmount = fraudAnalyzer.topFraudsAmount(3);
        IO.println("2. Top 3 Fraudes de Maior Valor: ");
        topFraudsAmount.stream().forEach(amount -> IO.println("%.2f".formatted(amount)));

        IO.println("3. Clientes Suspeitos: ");
        List<String> suspiciousClients = fraudAnalyzer.getSuspiciousClients(5);
        suspiciousClients.forEach(IO::println);

        BigDecimal totalLossAmount = fraudAnalyzer.sumLossAmout();
        IO.println("Prejuízo total: " + totalLossAmount);

        IO.println("5. Fraudes por Tipo: ");
        Map<TransactionType, Long> fraudsByType = fraudAnalyzer.getFraudsByType();
        fraudsByType.forEach((type, count) -> IO.println("- %s: %d".formatted(type, count)));

        IO.println("--------------");
        TransactionRepository transactionRepository;
        transactionRepository = new TransactionListRepository(transactions);
        String notExistingName = "C12345";
        transactionRepository.findByOriginName(notExistingName)
                .ifPresentOrElse(IO::println, () -> IO.println("Transação não encontrada para o cliente: " + notExistingName));
        String existingName = "C1231006815";
        transactionRepository.findByOriginName(existingName)
                .ifPresentOrElse(IO::println, () -> IO.println("Transação não encontrada para o cliente: " + existingName));

        long startTimeSearchingList = System.nanoTime();
        String existingNameAtTheEnd = "C1868032458";
        transactionRepository.findByOriginName(existingNameAtTheEnd)
                .ifPresentOrElse(IO::println, () -> IO.println("Transação não encontrada para o cliente: " + existingNameAtTheEnd));
        long endTimeSearchingList = System.nanoTime();
        IO.println("Tempo de busca na List em ms: " + (endTimeSearchingList - startTimeSearchingList) / 1_000_000.0);

        transactionRepository = new TransactionMapRepository(transactions);
        long startTimeSearchingMap = System.nanoTime();
        transactionRepository.findByOriginName(existingNameAtTheEnd)
                .ifPresentOrElse(IO::println, () -> IO.println("Transação não encontrada para o cliente: " + existingNameAtTheEnd));
        long endTimeSearchingMap = System.nanoTime();
        IO.println("Tempo de busca no Map em ms: " + (endTimeSearchingMap - startTimeSearchingMap) / 1_000_000.0);
    }
}

