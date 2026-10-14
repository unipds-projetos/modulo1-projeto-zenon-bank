package br.com.zenon.fraud;

import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import br.com.zenon.fraud.security.MethodGuard;

public final class DBMain {

    static final int INSERT_LIMIT = 10_000;
    private static final String PAYSIM = "data/PS_20174392719_1491204439457_log.csv";

    private DBMain() {
    }

    public static void main(String[] args) throws IOException, SQLException {
        MethodGuard.requireNoArguments(args);
        run(PAYSIM, System.out);
    }

    static void run(String fileName, PrintStream out) throws IOException, SQLException {
        MethodGuard.notNull(out, "out");
        List<Transaction> transactions = new TransactionIngestor().ingest(fileName, INSERT_LIMIT);
        try (TransactionSQLRepository repository = TransactionSQLRepository.open()) {
            repository.deleteAll();
            long start = System.nanoTime();
            repository.saveAll(transactions);
            long elapsedMs = (System.nanoTime() - start) / 1_000_000L;
            out.println("Transações inseridas: " + transactions.size());
            out.println("Tempo de inserção: " + elapsedMs + " ms");
            printSearch(repository, "C1231006815", out);
            printSearch(repository, "C12345", out);
        }
    }

    private static void printSearch(TransactionRepository repository, String originName, PrintStream out) {
        Optional<Transaction> transaction = repository.findByOriginName(originName);
        if (transaction.isPresent()) {
            out.println(transaction.get());
        } else {
            out.println("Transação não encontrada para o cliente " + originName);
        }
    }
}
