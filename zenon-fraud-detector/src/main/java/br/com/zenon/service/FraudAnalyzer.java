package br.com.zenon.service;

import br.com.zenon.domain.Transaction;
import br.com.zenon.domain.TransactionType;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    public List<BigDecimal> findHighestValueFraudsAmounts(int limitFraud) {
        return highValueFraudStream()
                .map(Transaction::amount)
                .limit(limitFraud)
                .toList();
    }

    public List<String> findTopSuspiciousClients(int limitName) {
        return highValueFraudStream()
                .map( transaction -> transaction.origin().name())
                .distinct()
                .limit(limitName)
                .toList();
    }


    public BigDecimal calculateTotalFraudLoss() {
        return fraudStream()
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<TransactionType,Long> countFraudsByType() {
        return fraudStream()
                .collect(Collectors.groupingBy(Transaction::type, Collectors.counting()));
    }

    private Stream<Transaction> fraudStream() {
        return transactions
                .stream()
                .filter(Transaction::isFraud);
    }

    private Stream<Transaction> highValueFraudStream() {
        return fraudStream()
                .sorted(Comparator.comparing(Transaction::amount).reversed());
    }
}
