package br.com.zenon.fraud;

import java.util.List;

public class DBMain {
    void main() {
        ConnectionFactory.getConnection();
        IO.println("<--------------->");
        IO.println("Connection with DB was sucessfully!");
        IO.println("<--------------->");
        String name1 = "C1231006815";
        String name2 = "C12345";

        TransactionSQLRepository transactionSQLRepository = new TransactionSQLRepository();
        var transactionIngestor = new TransactionIngestor();

        long startTimeSQL = System.nanoTime();

        List<Transaction> transactions = transactionIngestor.read("data/PS_20174392719_1491204439457_log.csv");
        //List<Transaction> transactions = transactionIngestor.read("paysim_with_bad_data.csv");
        IO.println(transactions.size());
        IO.println("<<<<<<<< Starting save transactions at Data Base >>>>>>>>");
        transactions.forEach(transactionSQLRepository::save);

        long endTimeSQL = System.nanoTime();
        IO.println("Tempo de busca no Banco de Dados em ms: " + (endTimeSQL - startTimeSQL) / 1_000_000.0);

        IO.println("<--------------->");
        transactionSQLRepository.findByOriginName(name2)
                .ifPresentOrElse(IO::println, () -> IO.println("Transaction not found for: " + name2));
        transactionSQLRepository.findByOriginName(name1)
                .ifPresentOrElse(IO::println, () -> IO.println("Transaction not found for: " + name1));
    }
}
