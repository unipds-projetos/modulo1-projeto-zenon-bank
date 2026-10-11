package br.com.zenon.transactions;

import java.math.BigDecimal;

public record TransactionCustomer(String name, BigDecimal oldBalance, BigDecimal newBalance) {

    public TransactionCustomer {
        if (name.isEmpty()) throw new IllegalArgumentException("name should not be empty");
        if(oldBalance.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("oldBalance should be positive: " + oldBalance);
        if(newBalance.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("newBalance should be positive: " + newBalance);

    }
}
