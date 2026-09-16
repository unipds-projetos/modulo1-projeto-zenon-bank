package br.com.zenon;

import java.util.*;

public class TransactionListRepository implements TransactionRepository {
    private List<Transaction> transactions = new ArrayList<>();


    public TransactionListRepository(List<Transaction> transactions){
        Objects.requireNonNull(transactions);
        this.transactions = transactions;
    }

    @Override
    public Optional<Transaction> findByOriginName(String name){
      return   transactions.stream().filter(transaction -> transaction.origin().name().equals(name)).findFirst();

    }

    @Override
    public void save(Transaction transaction) {
        this.transactions.add(transaction);
    }

}
