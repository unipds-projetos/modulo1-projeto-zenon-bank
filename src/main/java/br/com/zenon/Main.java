package br.com.zenon;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {

        Transaction t1 = new Transaction(1, TransactionType.PAYMENT, new BigDecimal("9834.64"),
                new TransactionCostumer("C1231006815", new BigDecimal("170136.0"), new BigDecimal("160296.36")),
                new TransactionCostumer("M1979787155", new BigDecimal("0.0"), new BigDecimal("0.0")),
                false, false);

        Transaction t2 = new Transaction(743, TransactionType.CASH_OUT, new BigDecimal("850002.52"),
                new TransactionCostumer("C1280323807", new BigDecimal("850002.52"), new BigDecimal("0.0")),
                new TransactionCostumer("C873221189", new BigDecimal("6510099.11"), new BigDecimal("7360101.63")),
                true, false);

        System.out.println(t1);
        System.out.println(t2);




    }
}