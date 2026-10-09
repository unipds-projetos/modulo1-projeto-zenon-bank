package br.com.zenon.fraud;

import java.util.List;
import java.util.Optional;

import br.com.zenon.fraud.security.MethodGuard;

public class TransactionListRepository implements TransactionRepository {

    private final List<Transaction> transactions;

    public TransactionListRepository(List<Transaction> transactions) {
        this.transactions = List.copyOf(MethodGuard.notNull(transactions, "transactions"));
    }

    @Override
    public Optional<Transaction> findByOriginName(String originName) {
        Optional<String> name = MethodGuard.customerQuery(originName);
        if (name.isEmpty()) {
            return Optional.empty();
        }
        for (Transaction transaction : transactions) {
            if (transaction.origin().name().equals(name.get())) {
                return Optional.of(transaction);
            }
        }
        return Optional.empty();
    }
}
