package br.com.zenon.fraud;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import br.com.zenon.fraud.security.MethodGuard;

public class TransactionMapRepository implements TransactionRepository {

    private final Map<String, Transaction> byOriginName;

    public TransactionMapRepository(List<Transaction> transactions) {
        Map<String, Transaction> index = new HashMap<>();
        for (Transaction transaction : List.copyOf(MethodGuard.notNull(transactions, "transactions"))) {
            index.putIfAbsent(transaction.origin().name(), transaction);
        }
        this.byOriginName = index;
    }

    @Override
    public void save(Transaction transaction) {
        MethodGuard.notNull(transaction, "transaction");
        byOriginName.putIfAbsent(transaction.origin().name(), transaction);
    }

    @Override
    public Optional<Transaction> findByOriginName(String originName) {
        return MethodGuard.customerQuery(originName).map(byOriginName::get);
    }
}
