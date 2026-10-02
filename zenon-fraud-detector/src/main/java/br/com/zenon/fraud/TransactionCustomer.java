package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.util.Objects;

public record TransactionCustomer(String name, BigDecimal oldBalance, BigDecimal newBalance) {
    public TransactionCustomer{
        if(name == null || name.trim().isEmpty()) throw new IllegalArgumentException("The value of 'name' cannot be null or empty: " + name);
        if(oldBalance.signum() < 0) throw new IllegalArgumentException("The value of 'oldBalance' must be equal or higher than zero: " + oldBalance);
        if(newBalance.signum() < 0) throw new IllegalArgumentException("The value of 'newBalance' must be equal or higher than zero: " + newBalance);
        Objects.requireNonNull(name);
        Objects.requireNonNull(oldBalance);
        Objects.requireNonNull(newBalance);
    }
}
