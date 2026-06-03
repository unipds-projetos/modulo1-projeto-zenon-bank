package br.com.zenon.repository;

import br.com.zenon.domain.Transaction;
import br.com.zenon.domain.TransactionRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TransactionListRepository implements TransactionRepository {

    private final List<Transaction> transactions;

    public TransactionListRepository(List<Transaction> transactions) {
        Objects.requireNonNull(transactions);
        this.transactions = transactions;
    }

    @Override
    public void save(Transaction transaction) {
        transactions.add(transaction);
    }

    @Override
    public Optional<Transaction> findByOriginName(String originName){
        return transactions
                .stream()
                .filter(transaction -> transaction.origin().name().equals(originName))
                .findFirst();
    }

}
