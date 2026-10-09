package br.com.zenon.fraud;

import java.math.BigDecimal;

final class Fixtures {

    private Fixtures() {
    }

    static Transaction transaction(
            int step,
            TransactionType type,
            String amount,
            String origin,
            boolean fraud,
            boolean flagged) {
        return new Transaction(
                step,
                type,
                new BigDecimal(amount),
                customer(origin, "1000.00", "0.00"),
                customer("M1", "0.00", "0.00"),
                fraud,
                flagged);
    }

    static Transaction fraud(String origin, String amount) {
        return transaction(1, TransactionType.TRANSFER, amount, origin, true, false);
    }

    static Transaction legitimate(String origin, String amount) {
        return transaction(1, TransactionType.PAYMENT, amount, origin, false, false);
    }

    static TransactionCustomer customer(String name, String oldBalance, String newBalance) {
        return new TransactionCustomer(name, new BigDecimal(oldBalance), new BigDecimal(newBalance));
    }
}
