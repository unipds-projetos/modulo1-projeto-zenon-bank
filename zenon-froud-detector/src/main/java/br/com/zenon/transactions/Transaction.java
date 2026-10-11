package br.com.zenon.transactions;

import br.com.zenon.enums.TransactionCategory;

import java.math.BigDecimal;

public record Transaction(int step, TransactionCategory type, BigDecimal amount, TransactionCustomer transactionCustumerOrigin,
                          TransactionCustomer transactionCustumerRecipient, boolean isFraud, boolean isFlaggedFraud) {


public  Transaction{
    if (step <= 0) throw new IllegalArgumentException("step should be positive: " + step);
    if(amount.compareTo(BigDecimal.ZERO) < 0) throw  new IllegalArgumentException("amount should be positive: " + amount);
}

}
