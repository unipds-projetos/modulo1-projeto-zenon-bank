package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class FraudAnalyzer {

    List<Transaction> transactions;

    public FraudAnalyzer(List<Transaction> transactions) {
        if (transactions == null) {
            throw new IllegalArgumentException("Lista de transações não pode ser nula");

        }
        this.transactions = transactions;
    }

    public long countFrauds() {
        return transactions.stream()
                .filter(Transaction::isFraud)
                .count();
    }

    public List<Transaction> findTopAmounts(int limit) {
        return transactions.stream()
                .filter(Transaction::isFraud)
                .sorted(Comparator.comparing(Transaction::amount).reversed())
                .limit(limit)
                .toList();
    }

    public List<String> findTopSuspiciousClients(int limit) {
        return transactions.stream()
                .filter(Transaction::isFraud)
                .sorted(Comparator.comparing(Transaction::amount).reversed())
                .map(transaction -> transaction.origin().name())
                .distinct()
                .limit(limit)
                .toList();
    }

    public List<Transaction> findTopSuspects(int limit) {
        return transactions.stream()
                .filter(Transaction::isFraud)
                .sorted(Comparator.comparing(Transaction::amount).reversed())
                .limit(limit)
                .toList();
    }

    public BigDecimal prejuzioTotal(List<Transaction> transactions) {
        return transactions.stream()
                .filter(Transaction::isFraud)
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<EnumTransactionType, Long> quantidadePorTipo() {
        return transactions.stream()
                .filter(Transaction::isFraud)
                .collect(Collectors.groupingBy(Transaction::type, Collectors.counting()));

    }
}
