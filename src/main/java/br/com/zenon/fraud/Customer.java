package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.util.Objects;

public record Customer(String name, BigDecimal oldBalance, BigDecimal newBalance) {

    public Customer {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(oldBalance, "oldBalance");
        Objects.requireNonNull(newBalance, "newBalance");
    }
}
