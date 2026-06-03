package br.com.zenon.domain;

import java.math.BigDecimal;
import java.util.Objects;

public record Transaction(int step, TransactionType type, BigDecimal amount, TransactionCustumer origin,
                          TransactionCustumer recipient, boolean isFraud, boolean isFlaggedFraud){

    public Transaction{
        Objects.requireNonNull(type);
        Objects.requireNonNull(origin);
        Objects.requireNonNull(recipient);
        Objects.requireNonNull(amount);

        if(step <= 0) throw new IllegalArgumentException("O valor de step deve ser maior que zero: " + step);
        if(amount.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("O valor de amount deve ser maior que zero: " + amount);
    }

}
