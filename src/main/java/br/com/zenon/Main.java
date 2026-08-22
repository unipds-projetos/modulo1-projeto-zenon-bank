package br.com.zenon;

import java.math.BigDecimal;
import java.util.List;

public class Main {
    public static void main(String[] args) {


        
        List<Transaction> transactions = new TransactionIngestor().readNew("data/PS_20174392719_1491204439457_log.csv");

        transactions.stream().limit(10).forEach(System.out::println);



        List<Transaction> transactionsBadData = new TransactionIngestor().readNew("data/paysim_with_bad_data.csv");

        System.out.println(transactionsBadData.size());
        transactionsBadData.forEach(System.out::println);




    }
}