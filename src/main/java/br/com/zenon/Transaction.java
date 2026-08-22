package br.com.zenon;

import java.math.BigDecimal;
import java.util.Objects;

import static java.lang.String.valueOf;

public record Transaction(int step, TransactionType type, BigDecimal amount, TransactionCostumer origin, TransactionCostumer recipient,
                           Boolean isFraud, boolean isFlaggedFraud) {

  public Transaction{

   Objects.requireNonNull(type);
   Objects.requireNonNull(amount);
   Objects.requireNonNull(origin);
   Objects.requireNonNull(recipient);

   if(step <= 0) throw new IllegalArgumentException("step should be positive"+ step);
   if(amount.signum() < 0) throw new IllegalArgumentException("value of amount should pe positive");






  }
};

