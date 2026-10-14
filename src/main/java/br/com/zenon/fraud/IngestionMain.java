package br.com.zenon.fraud;

import java.io.IOException;
import java.io.PrintStream;
import java.math.RoundingMode;
import java.sql.SQLException;

import br.com.zenon.fraud.security.MethodGuard;

public final class IngestionMain {

    static final int PARALLELISM = 4;
    private static final String PAYSIM = "data/PS_20174392719_1491204439457_log.csv";

    private IngestionMain() {
    }

    public static void main(String[] args) throws IOException, SQLException {
        MethodGuard.requireNoArguments(args);
        run(PAYSIM, System.out);
    }

    static void run(String fileName, PrintStream out) throws IOException, SQLException {
        run(fileName, out, EfficientTransactionIngestor.platformThreads(PARALLELISM), PARALLELISM);
    }

    static void run(
            String fileName,
            PrintStream out,
            EfficientTransactionIngestor ingestor,
            int connections) throws IOException, SQLException {
        MethodGuard.notNull(ingestor, "ingestor");
        MethodGuard.notNull(out, "out");
        try (ingestor) {
            try (TransactionSQLRepository repository = TransactionSQLRepository.open()) {
                repository.deleteAll();
            }
            long start = System.nanoTime();
            try (InsertConnections pool = InsertConnections.open(connections)) {
                ingestor.readBatch(fileName, pool::saveAll);
            }
            long elapsedMs = (System.nanoTime() - start) / 1_000_000L;
            try (TransactionSQLRepository repository = TransactionSQLRepository.open()) {
                DatasetTotals totals = repository.totals();
                out.println("Transações inseridas: " + totals.transactions());
                out.println("Total de fraudes: " + totals.frauds());
                out.println("Valor total transacionado: "
                        + totals.totalAmount().setScale(2, RoundingMode.HALF_UP).toPlainString());
                out.println("Tempo total: " + elapsedMs + " ms");
            }
        }
    }
}
