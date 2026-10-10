package br.com.zenon.transactions;

import java.math.BigDecimal;

public record TransactionCustumer(String name, BigDecimal oldBalance, BigDecimal newBalance) {
}
