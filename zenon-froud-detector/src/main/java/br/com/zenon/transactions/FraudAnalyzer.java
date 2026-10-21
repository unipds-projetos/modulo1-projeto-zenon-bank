package br.com.zenon.transactions;

import br.com.zenon.dto.FraudStatistics;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FraudAnalyzer {
    private final List<Transaction> transactions;

    public FraudAnalyzer(String fileName) {
        TransactionIngetor transactionIngetor = new TransactionIngetor();
        this.transactions = transactionIngetor.read(fileName);
    }

    public FraudStatistics calculateStatistics() {
        return transactions.stream()
                .collect(
                        FraudStatistics::new,
                        FraudStatistics::accumulate,
                        FraudStatistics::combine
                );
    }

    public List<BigDecimal> TopThreeFraud(){
        return transactions.stream()
                .filter(Transaction::isFraud)
                .sorted(Comparator.comparing(Transaction::amount).reversed())
                .map(Transaction::amount)
                .limit(3)
                .toList();
    }

    public Set<String> FlaggedCustomer(){
        return transactions.stream()
                .filter(Transaction::isFraud)
                .sorted(Comparator.comparing(Transaction::amount).reversed())
                .flatMap(t -> Stream.of(t.transactionCustumerOrigin().name()))
                .limit(5)
                .collect(Collectors.toSet());
    }
}