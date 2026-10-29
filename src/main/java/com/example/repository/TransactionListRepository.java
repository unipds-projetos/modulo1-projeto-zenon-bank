package com.example.repository;

import com.example.dto.TransactionDTO;

import java.util.Optional;

public interface TransactionListRepository {
    Optional<TransactionDTO> getTransactionByOriginClientName(String originClientName);

}
