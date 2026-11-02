package br.com.zenon;

import java.math.BigDecimal;

public record TransactionCustomer(String nameOrig, BigDecimal oldBalance, BigDecimal newBalance) {

}
