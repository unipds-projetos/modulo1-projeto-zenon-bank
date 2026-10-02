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
    public Transaction{
        if(step <= 0) throw new IllegalArgumentException("The value of 'step' must be positive: " + step);
        if(amount.signum() < 0) throw new IllegalArgumentException("The value of 'amount' must be equal or higher than zero: " + amount);
        Objects.requireNonNull(type);
        Objects.requireNonNull(amount);
        Objects.requireNonNull(origin);
        Objects.requireNonNull(recipient);
    }
}