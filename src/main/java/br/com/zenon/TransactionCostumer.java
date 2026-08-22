package br.com.zenon;

import java.math.BigDecimal;
import java.util.Objects;

public record TransactionCostumer(String name,  BigDecimal oldBalance, BigDecimal newBalance) {

    public TransactionCostumer{

        Objects.requireNonNull(name);
        Objects.requireNonNull(newBalance);
        Objects.requireNonNull(oldBalance);

        if(oldBalance.signum() < 0) throw  new IllegalArgumentException("value of old bAlance shoulb be positive or zero");
        if(newBalance.signum() < 0) throw  new IllegalArgumentException("value of new bAlance shoulb be positive or zero");
        if(name.trim().isEmpty()) throw  new IllegalArgumentException("value of name cant be empty");
    }


}
