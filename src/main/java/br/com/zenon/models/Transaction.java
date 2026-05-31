package br.com.zenon.models;

import br.com.zenon.models.types.TransactionType;

import java.math.BigDecimal;
import java.util.Objects;

public record Transaction(
        int step,
        TransactionType type,
        BigDecimal amount,
        Origin origin,
        Origin destinatary,
        FraudDemark fraudDemark
) {
    public Transaction {
        Objects.requireNonNull(type);
        Objects.requireNonNull(amount);
        Objects.requireNonNull(origin);
        Objects.requireNonNull(destinatary);
        Objects.requireNonNull(fraudDemark);
    }
}
