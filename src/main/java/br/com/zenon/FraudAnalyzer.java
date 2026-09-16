package br.com.zenon;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FraudAnalyzer {

    private  List<Transaction> transactions;

    public FraudAnalyzer(List<Transaction> transactions) {
        Objects.requireNonNull(transactions);
        this.transactions = transactions;
    }

    public long countFrauds(){
//        return transactions.stream().filter(v -> v.isFraud()).count();
        return fraudStream().count();


    }
    public Map<TransactionType, Long> countFraudsByType(){
//        return transactions.stream().filter(v -> v.isFraud()).count();
        return fraudStream().collect(Collectors.groupingBy(Transaction::type, Collectors.counting()));
    }

    public List<Transaction> getTopFrauds(int limit) {
        return highestValueStream()
                .limit(limit)
                .toList();
    }


    public List<String> findSuspects(int limit){
        return highestValueStream()
                .map(transaction -> transaction.origin().name())
                .distinct()
                .limit(limit)
                .toList();
    }

    public BigDecimal totalDamage(){
        return fraudStream().map(Transaction::amount).reduce(BigDecimal.ZERO,BigDecimal::add);
    }

    public double totalDamage2(){
        return fraudStream().mapToDouble(t->t.amount().doubleValue()).sum();
    }

    private Stream<Transaction> fraudStream() {
        return transactions.stream()
                .filter(Transaction::isFraud);
    }
    private Stream<Transaction> highestValueStream() {
        return fraudStream()
                .sorted(Comparator.comparing(Transaction::amount).reversed());
    }
}
