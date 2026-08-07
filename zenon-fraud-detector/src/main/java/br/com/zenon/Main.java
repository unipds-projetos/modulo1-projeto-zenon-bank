package br.com.zenon;

import java.math.BigDecimal;

public class Main {
    static void main() {

        Customer origin2 = new Customer(
                "C1280323807",
                BigDecimal.valueOf(6510099.11),
                BigDecimal.valueOf(7360101.63)
        );
        Customer destination2 = new Customer(
                "C873221189",
                BigDecimal.valueOf(850002.52),
                BigDecimal.ZERO
        );
        Customer origin1 = new Customer(
                "C1231006815",
                BigDecimal.valueOf(170136.0),
                BigDecimal.valueOf(160296.36)

        );
        Customer destination1 = new Customer(
                "M1979787155",
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
        Transaction t1 = new Transaction(
                1,
                TransactionType.TRANSFER,
                BigDecimal.valueOf(9839.64),
                origin1,
                destination1,
                false,
                false
        );

        Transaction t2 = new Transaction(
                743,
                TransactionType.TRANSFER,
                BigDecimal.valueOf(850002.52),
                origin2,
                destination2,
                true,
                false
        );
        IO.println(t1);
        IO.println(t2);
    }
}
