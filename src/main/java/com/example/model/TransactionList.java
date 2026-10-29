package com.example.model;

import com.example.dto.TransactionDTO;
import com.example.repository.TransactionListRepository;

import java.util.List;
import java.util.Optional;

public class TransactionList implements TransactionListRepository {

    private final List<TransactionDTO> transactions;

    public TransactionList(List<TransactionDTO> transactions) {
        this.transactions = transactions;
    }

    @Override
    public Optional<TransactionDTO> getTransactionByOriginClientName(String originClientName) {
        return transactions.stream()
                .filter(transactionDTO -> transactionDTO.origin().name().equals(originClientName))
                .findFirst();
    }
}
