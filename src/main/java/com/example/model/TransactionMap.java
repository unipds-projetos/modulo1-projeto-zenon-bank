package com.example.model;

import com.example.dto.TransactionDTO;
import com.example.repository.TransactionListRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TransactionMap implements TransactionListRepository {

    private final Map<String, TransactionDTO> transactionByOriginName = new HashMap<>();

    public TransactionMap(List<TransactionDTO> transactions) {
        for (TransactionDTO t : transactions) {
            transactionByOriginName.putIfAbsent(t.origin().name(), t);
        }
    }

    @Override
    public Optional<TransactionDTO> getTransactionByOriginClientName(String originClientName) {
        return Optional.ofNullable(transactionByOriginName.get(originClientName));
    }
}