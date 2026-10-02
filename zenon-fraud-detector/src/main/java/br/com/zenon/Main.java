package br.com.zenon;

import java.math.BigDecimal;

public class Main {
    void  main () {
        var t1 = new Transaction(1, TransactionType.PAYMENT, new BigDecimal("100.00"),
                                 new TransactionCustumer("origem", new BigDecimal("100.00"), new BigDecimal("100.00")),
                                    new TransactionCustumer("destino", new BigDecimal("100.00"), new BigDecimal("100.00")),
                                false, false);

        var t2 = new Transaction(1, TransactionType.CASH_OUT, new BigDecimal("100.00"),
                new TransactionCustumer("origem", new BigDecimal("100.00"), new BigDecimal("100.00")),
                new TransactionCustumer("destino", new BigDecimal("100.00"), new BigDecimal("100.00")),
                false, false);

        IO.println(t1);
        IO.println(t2);
    }

}
