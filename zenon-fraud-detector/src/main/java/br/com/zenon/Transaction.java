package br.com.zenon;

import java.math.BigDecimal;

public record Transaction(int step, TransactionType type, BigDecimal amount, TransactionCustumer origin,
                          TransactionCustumer recipient, boolean isFraud, boolean isFlaggedFraud){

}
