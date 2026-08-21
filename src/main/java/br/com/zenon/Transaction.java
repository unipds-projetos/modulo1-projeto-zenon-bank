package br.com.zenon;

import java.math.BigDecimal;

 public record Transaction(int step, TransactionType type, BigDecimal amount, TransactionCostumer origin, TransactionCostumer recipient,
                           Boolean isFraud, boolean isFlaggedFraud) {
};

