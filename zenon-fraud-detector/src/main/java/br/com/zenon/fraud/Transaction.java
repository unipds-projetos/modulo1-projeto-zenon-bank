package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.util.Objects;

public record Transaction(
        int step,
        TransactionType type,
        BigDecimal amount,
        TransactionCustomer origin,
        TransactionCustomer recipient,
        boolean isFraud,
        boolean isFlaggedFraud
) {
    public Transaction {
        if (step < 1) {
            throw new IllegalArgumentException("step should be positive: " + step);
        }
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(origin, "origin");
        Objects.requireNonNull(recipient, "recipient");
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("amount should be positive: " + amount);
        }
    }
}
