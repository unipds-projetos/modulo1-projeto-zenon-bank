package br.com.zenon;

import java.util.Optional;

public interface TransactionRepository {
    void save(Transaction transaction);
    Optional<Transaction> findByOriginName(String originName);
}
