package br.com.zenon;

import java.math.BigDecimal;
import java.util.Objects;

public record TransactionCustumer (String name, BigDecimal oldBalance, BigDecimal newBalance) {

    public TransactionCustumer{
        Objects.requireNonNull(name);
        Objects.requireNonNull(oldBalance);
        Objects.requireNonNull(newBalance);

        if(oldBalance.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("O valor de oldBalance deve ser maior que zero: " + oldBalance);
        if(newBalance.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("O valor de newBalance deve ser maior que zero: " + newBalance);
        if(name.trim().isEmpty()) throw new IllegalArgumentException("O nome não pode ser nulo ou vazio");
    }

}
