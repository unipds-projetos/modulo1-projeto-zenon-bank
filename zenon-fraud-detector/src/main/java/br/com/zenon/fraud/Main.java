package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.util.List;

public class Main{
    void main(){
        var transaction1 = new Transaction(1, TransactionType.PAYMENT, new BigDecimal("9838.64"),
        new TransactionCustomer("C1231006815", new BigDecimal("170136.0"), new BigDecimal("160296.36")),
        new TransactionCustomer("M1979787155", new BigDecimal("0.0"), new BigDecimal("0.0")),
        false, false
        );

        var transaction2 = new Transaction(743, TransactionType.CASH_OUT, new BigDecimal("850002.52"),
        new TransactionCustomer("C1280323807", new BigDecimal("850002.52"), new BigDecimal("0.0")),
        new TransactionCustomer("C873221189", new BigDecimal("6510099.11"), new BigDecimal("7360101.63")),
        true, false
        );

        IO.println(transaction1);
        IO.println(transaction2);
        IO.println("--------------");
        var transactionIngestor = new TransactionIngestor();
        List<Transaction> transactions = transactionIngestor.read("data/PS_20174392719_1491204439457_log.csv");

        transactions.stream().limit(10).forEach(IO::println);
        //IO.println(transactions.size());

        IO.println("--------------");
        List<Transaction> transactionsBadData = transactionIngestor.read("data/paysim_with_bad_data.csv");
        IO.println(transactionsBadData.size());
        transactionsBadData.stream().forEach(IO::println);
    }
}

