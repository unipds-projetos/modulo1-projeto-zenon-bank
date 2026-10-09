package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import br.com.zenon.fraud.security.MethodGuard;

public class FraudAnalyzer {

    private final List<Transaction> transactions;

    public FraudAnalyzer(List<Transaction> transactions) {
        this.transactions = List.copyOf(MethodGuard.notNull(transactions, "transactions"));
    }

    public long fraudCount() {
        return transactions.stream()
                .filter(Transaction::isFraud)
                .count();
    }

    public List<BigDecimal> largestFraudAmounts(int limit) {
        int bounded = MethodGuard.limit(limit);
        return transactions.stream()
                .filter(Transaction::isFraud)
                .map(Transaction::amount)
                .sorted(Comparator.reverseOrder())
                .limit(bounded)
                .toList();
    }

    public List<String> largestSuspiciousCustomers(int limit) {
        int bounded = MethodGuard.limit(limit);
        return transactions.stream()
                .filter(Transaction::isFraud)
                .sorted(Comparator.comparing(Transaction::amount).reversed())
                .map(transaction -> transaction.origin().name())
                .distinct()
                .limit(bounded)
                .toList();
    }

    public BigDecimal totalLoss() {
        return transactions.stream()
                .filter(Transaction::isFraud)
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<TransactionType, Long> fraudsByType() {
        return Map.copyOf(transactions.stream()
                .filter(Transaction::isFraud)
                .collect(Collectors.groupingBy(Transaction::type, Collectors.counting())));
    }
}
