package br.com.zenon.models;

import java.math.BigDecimal;
import java.util.Objects;

public record Origin(
        String name,
        BigDecimal oldBalance,
        BigDecimal newBalance
) {
    public Origin {
        Objects.requireNonNull(name);
        Objects.requireNonNull(oldBalance);
        Objects.requireNonNull(newBalance);
    }
}
