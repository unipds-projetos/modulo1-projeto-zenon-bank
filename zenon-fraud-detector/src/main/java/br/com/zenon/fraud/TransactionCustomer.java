package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.util.Objects;

public record TransactionCustomer(
        String name,
        BigDecimal oldBalance,
        BigDecimal newBalance
) {
    public TransactionCustomer {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name should not be empty");
        }
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
