package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.util.Objects;

public record Transaction(
        int step,
        EnumTransactionType type,
        BigDecimal amount,
        TransactionCustomer origin,
        TransactionCustomer recipient,
        boolean isFraud,
        boolean isFlaggedFraud
) {

    public Transaction {
        Objects.requireNonNull(type, "Valor de 'type' não pode ser nulo");
        Objects.requireNonNull(amount, "Valor de 'amount' não pode ser nulo");
        if (step < 1) throw new IllegalArgumentException("Valor de 'step' deve ser maior ou igual a 1: " + step);
        if (amount.signum() < 0) throw new IllegalArgumentException("Valor de 'amount' é inválido: " + amount);
    }

}
