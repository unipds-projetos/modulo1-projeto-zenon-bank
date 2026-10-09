package br.com.zenon.fraud;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        String fileName = "data/paysim_with_bad_data.csv";
        List<Transaction> transactions = new TransactionIngestor().ingest(fileName);

        System.out.println(transactions.size());
        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }
}
