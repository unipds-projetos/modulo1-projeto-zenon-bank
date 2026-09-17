package br.com.zenon.fraud;

import java.util.List;

public class Main {

    private static final String PAYSIM_BAD_DATA_CSV = "data/paysim_with_bad_data.csv";

    public static void main(String[] args) {
        TransactionIngestor ingestor = new TransactionIngestor();
        List<Transaction> transactions = ingestor.read(PAYSIM_BAD_DATA_CSV);

        System.out.println(transactions.size());
        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }
}
