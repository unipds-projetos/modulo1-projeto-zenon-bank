package br.com.zenon;

import java.math.BigDecimal;
import java.util.List;

public class DBMain {

    void main(){

        ConnectionFactory.getConnection();
        IO.println("Conexão criada com o DB");

        var repository = new TransactionSQLRepository();

        var transactionIngestor = new TransactionIngestor();

        long startTimeSQL = System.nanoTime();
        List<Transaction> transactions = transactionIngestor.read("data/PS_20174392719_1491204439457_log.csv");

        IO.println("Iniciando adição das transacoes no banco de dados");
        IO.println("Total de transacoes: " + transactions.size());

        transactions.forEach(repository::save);

        long endTimeSQL = System.nanoTime();

        IO.println("Tempo de inserção - SQL (ms) " + (endTimeSQL - startTimeSQL) / 1_000_000.0);

        repository.findByOriginName("C1231006815")
                .ifPresentOrElse(IO::println, () -> IO.println("Transacao nao encontrada para: C1231006815"));

    }


}
