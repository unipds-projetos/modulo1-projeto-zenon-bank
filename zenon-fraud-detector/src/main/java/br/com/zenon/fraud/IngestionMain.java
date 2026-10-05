package br.com.zenon.fraud;

import java.util.List;

public class IngestionMain {
    void main() {
        TransactionSQLRepository transactionSQLRepository = new TransactionSQLRepository();
        var transactionIngestor = new TransactionIngestor();
        var efficientTransactionIngestor = new EfficientTransactionIngestor();

        long startTimeSQL = System.nanoTime();
        IO.println("<<<<<<<< Starting save transactions at Data Base >>>>>>>>");

        //List<Transaction> transactions = transactionIngestor.read("data/PS_20174392719_1491204439457_log.csv");
        efficientTransactionIngestor.readAsBatch("data/PS_20174392719_1491204439457_log.csv",
                transactionSQLRepository::saveAll);
        //transactions.forEach(transactionSQLRepository::save);

        long endTimeSQL = System.nanoTime();
        IO.println("Tempo de busca no Banco de Dados em ms: " + (endTimeSQL - startTimeSQL) / 1_000_000.0);

    }
}
