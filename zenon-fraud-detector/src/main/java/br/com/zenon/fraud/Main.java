package br.com.zenon.fraud;

import java.util.List;

public class Main {

    private static final String PAYSIM_CSV = "data/PS_20174392719_1491204439457_log.csv";

    public static void main(String[] args) {
        TransactionIngestor ingestor = new TransactionIngestor();
        List<Transaction> transactions = ingestor.read(PAYSIM_CSV);

        int printed = Math.min(10, transactions.size());
        for (int i = 0; i < printed; i++) {
            System.out.println(transactions.get(i));
        }
    }
}
