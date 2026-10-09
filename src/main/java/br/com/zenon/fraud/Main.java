package br.com.zenon.fraud;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        String fileName = "data/PS_20174392719_1491204439457_log.csv";
        List<Transaction> transactions = new TransactionIngestor().ingest(fileName);

        int limit = Math.min(10, transactions.size());
        for (int i = 0; i < limit; i++) {
            System.out.println(transactions.get(i));
        }
    }
}
