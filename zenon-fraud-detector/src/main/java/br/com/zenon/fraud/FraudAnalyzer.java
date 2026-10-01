package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FraudAnalyzer {
    private final List<Transaction> transactions;

    public FraudAnalyzer(List<Transaction> transactions){
        Objects.requireNonNull(transactions);
        this.transactions = transactions;
    }

    public long countFrauds() {
        return fraudStream()
                .count();
    }

    public List<BigDecimal> topFraudsAmount(int limit) {
        return highestValueFraudStream()
                .map(Transaction::amount)
                .limit(limit)
                .toList();
    }

    public List<String> getSuspiciousClients(int limit) {
        return  highestValueFraudStream()
                .map(transaction -> transaction.origin().name())
                .distinct()
                .limit(limit)
                .toList();
    }

    public BigDecimal sumLossAmout() {
        return fraudStream()
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<TransactionType, Long> getFraudsByType() {
        return fraudStream()
                .collect(Collectors.groupingBy(
                        Transaction::type, Collectors.counting()
                ));
    }

    private Stream<Transaction> fraudStream(){
        return transactions.stream()
                .filter(Transaction::isFraud);
    }

    private Stream<Transaction> highestValueFraudStream(){
        return fraudStream()
                .sorted(Comparator.comparing(Transaction::amount).reversed());
    }
}
