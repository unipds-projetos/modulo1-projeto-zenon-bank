package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.util.Objects;

public record TransactionCustomer(String name, BigDecimal oldBalance, BigDecimal newBalance) {

    public TransactionCustomer {
        Objects.requireNonNull(name, "Valor de 'name' não pode ser nulo");
        Objects.requireNonNull(oldBalance, "Valor de 'oldBalance' não pode ser nulo");
        Objects.requireNonNull(newBalance, "Valor de 'newBalance' não pode ser nulo");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Valor de 'name' é inválido: " + name);
        if (oldBalance.signum() < 0) throw new IllegalArgumentException("Valor de 'oldBalance' é inválido: " + oldBalance);
        if (newBalance.signum() < 0) throw new IllegalArgumentException("Valor de 'newBalance' é inválido: " + newBalance);
    }
}
