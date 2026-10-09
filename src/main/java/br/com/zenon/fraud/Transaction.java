package br.com.zenon.fraud;

import java.math.BigDecimal;

import br.com.zenon.fraud.security.MethodGuard;

public record Transaction(
        int step,
        TransactionType type,
        BigDecimal amount,
        TransactionCustomer origin,
        TransactionCustomer recipient,
        boolean isFraud,
        boolean isFlaggedFraud) {

    public Transaction {
        step = MethodGuard.step(step);
        type = MethodGuard.notNull(type, "type");
        amount = MethodGuard.money(amount, "amount");
        origin = MethodGuard.notNull(origin, "origin");
        recipient = MethodGuard.notNull(recipient, "recipient");
    }
}
