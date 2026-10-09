package br.com.zenon.fraud;

import java.math.BigDecimal;

import br.com.zenon.fraud.security.MethodGuard;

public record TransactionCustomer(String name, BigDecimal oldBalance, BigDecimal newBalance) {

    public TransactionCustomer {
        name = MethodGuard.customerName(name);
        oldBalance = MethodGuard.money(oldBalance, "oldBalance");
        newBalance = MethodGuard.money(newBalance, "newBalance");
    }
}
