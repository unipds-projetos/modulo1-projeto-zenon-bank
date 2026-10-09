package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.util.Objects;

public record TransactionCustomer(String name, BigDecimal oldBalance, BigDecimal newBalance) {

    public TransactionCustomer {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(oldBalance, "oldBalance");
        Objects.requireNonNull(newBalance, "newBalance");
    }
}
