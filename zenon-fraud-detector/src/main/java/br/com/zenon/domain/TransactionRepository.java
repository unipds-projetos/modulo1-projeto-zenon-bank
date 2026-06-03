package br.com.zenon.domain;

import java.util.Optional;

public interface TransactionRepository {
    void save(Transaction transaction);
    Optional<Transaction> findByOriginName(String originName);
}
