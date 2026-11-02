package br.com.zenon;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FraudAnalyzer {

	private final List<Transaction> transactions;

	public FraudAnalyzer(List<Transaction> transactions) {
		Objects.nonNull(transactions);
		this.transactions = transactions;
	}

	public long countFrauds() {
		return getTransactionStream().count();
	}

	public List<BigDecimal> findHighestValueFraudAmount(int i) {

		return getTransactionStream().sorted(Comparator.comparing(Transaction::amount).reversed())
				.map(Transaction::amount).limit(i).toList();
	}

	public List<String> findTopSuspectsClients(int i) {
		return getTransactionStream().sorted(Comparator.comparing(Transaction::amount).reversed())
				.map(transaction -> transaction.origin().nameOrig()).distinct().limit(i).toList();
	}

	public BigDecimal calculateTotalFraudLoss() {
		return getTransactionStream().map(Transaction::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public Map<TransactionType, Long> countFraudsByType() {
		return getTransactionStream().collect(Collectors.groupingBy(Transaction::type, Collectors.counting()));
	}

	private Stream<Transaction> getTransactionStream() {
		return transactions.stream().filter(Transaction::isFraud);
	}

}
