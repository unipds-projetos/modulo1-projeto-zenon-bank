package br.com.zenon.fraud;

import java.math.BigDecimal;

public class Main {

    public static void main(String[] args) {
        Transaction payment = new Transaction(
                1,
                TransactionType.PAYMENT,
                new BigDecimal("9839.64"),
                new Customer("C1231006815", new BigDecimal("170136.0"), new BigDecimal("160296.36")),
                new Customer("M1979787155", new BigDecimal("0.0"), new BigDecimal("0.0")),
                false,
                false);

        Transaction cashOut = new Transaction(
                743,
                TransactionType.CASH_OUT,
                new BigDecimal("850002.52"),
                new Customer("C1280323807", new BigDecimal("850002.52"), new BigDecimal("0.0")),
                new Customer("C873221189", new BigDecimal("6510099.11"), new BigDecimal("7360101.63")),
                true,
                false);

        System.out.println(payment);
        System.out.println(cashOut);
    }
}
