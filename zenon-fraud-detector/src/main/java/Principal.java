import br.com.zenon.fraud.TransactionCustomer;
import br.com.zenon.fraud.EnumTransactionType;
import br.com.zenon.fraud.Transaction;

import java.math.BigDecimal;

class Principal {
    public static void main(String[] args) {
        var transaction1 = new Transaction(
                "1",
                EnumTransactionType.PAYMENT,
                new BigDecimal("9839.64"),
                new TransactionCustomer("C1231006815", new BigDecimal("170136.0"), new BigDecimal("160296.36")),
                new TransactionCustomer("M1979787155", new BigDecimal("0.0"), new BigDecimal("0.0")),
                false,
                false
        );

        var transaction2= new Transaction(
                "743",
                EnumTransactionType.CASH_OUT,
                new BigDecimal("850002.52"),
                new TransactionCustomer("C1280323807", new BigDecimal("850002.52"), new BigDecimal("0.0")),
                new TransactionCustomer("C873221189", new BigDecimal("6510099.11"), new BigDecimal("7360101.63")),
                true,
                false
        );
        System.out.println("Olá Mundo");
        System.out.println(transaction1.origin().name());
    }
}
