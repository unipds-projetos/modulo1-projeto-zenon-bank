package br.com.zenon;

import java.math.BigDecimal;

public record TransactionCostumer(String name,  BigDecimal oldBalance, BigDecimal newBalance) {


}
