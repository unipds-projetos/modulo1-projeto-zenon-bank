package br.com.zenon;

import java.math.BigDecimal;

public record TransactionCustumer (String name, BigDecimal oldBalance, BigDecimal newBalance) {

}
