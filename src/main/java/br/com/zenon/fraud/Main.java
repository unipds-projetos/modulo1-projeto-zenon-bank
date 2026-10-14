package br.com.zenon.fraud;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Optional;

import br.com.zenon.fraud.security.MethodGuard;

public class Main {

    public static void main(String[] args) throws IOException {
        MethodGuard.requireNoArguments(args);
        run("data/PS_20174392719_1491204439457_log.csv", System.out);
    }

    static void run(String fileName, PrintStream out) throws IOException {
        MethodGuard.notNull(out, "out");
        List<Transaction> transactions = new TransactionIngestor().ingest(fileName);

        TransactionRepository listRepository = new TransactionListRepository(transactions);
        printSearch(listRepository, "C12345", out);
        printSearch(listRepository, "C1231006815", out);

        String worstCase = transactions.getLast().origin().name();
        out.println("Busca na lista por " + worstCase + ": " + measure(listRepository, worstCase) + " ns");

        TransactionRepository mapRepository = new TransactionMapRepository(transactions);
        out.println("Busca no mapa por " + worstCase + ": " + measure(mapRepository, worstCase) + " ns");
    }

    private static void printSearch(TransactionRepository repository, String originName, PrintStream out) {
        Optional<Transaction> transaction = repository.findByOriginName(originName);
        if (transaction.isPresent()) {
            out.println(transaction.get());
        } else {
            out.println("Transação não encontrada para o cliente " + originName);
        }
    }

    private static long measure(TransactionRepository repository, String originName) {
        long start = System.nanoTime();
        repository.findByOriginName(originName);
        return System.nanoTime() - start;
    }
}
