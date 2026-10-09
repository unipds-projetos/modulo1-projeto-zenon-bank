package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

public record TransactionCustomer(String name, BigDecimal oldBalance, BigDecimal newBalance) {

    public TransactionCustomer {
        name = Optional.ofNullable(name)
                .filter(value -> !value.isBlank())
                .orElseThrow(() -> new IllegalArgumentException("name should not be empty"));
        Objects.requireNonNull(oldBalance, "oldBalance");
        Objects.requireNonNull(newBalance, "newBalance");
        if (oldBalance.signum() < 0) {
            throw new IllegalArgumentException("oldBalance should be positive: " + oldBalance);
        }
        if (newBalance.signum() < 0) {
            throw new IllegalArgumentException("newBalance should be positive: " + newBalance);
        }
    }
}
