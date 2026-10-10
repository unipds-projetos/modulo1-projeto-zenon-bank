package br.com.zenon.transactions;

import br.com.zenon.enums.TransactionCategory;

import java.math.BigDecimal;

public record Transaction(int step, TransactionCategory type, BigDecimal amount, TransactionCustumer transactionCustumerOrigin,
                          TransactionCustumer transactionCustumerRecipient, boolean isFroud, boolean isFlaggedFraud) {

}
