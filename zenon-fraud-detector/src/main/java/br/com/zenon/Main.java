package br.com.zenon;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class Main {
    void  main () {
/*
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
*/

        TransctionIngestor t1 = new TransctionIngestor();
        t1.setArquivo("C:\\Java\\POS\\FundamentosJava\\modulo1-projeto-zenon-bank\\zenon-fraud-detector\\data\\PS_20174392719_1491204439457_log.csv");
        List<Transaction> list = t1.transctions();
        list.stream().limit(2710).forEach(System.out::println);
    }

}
