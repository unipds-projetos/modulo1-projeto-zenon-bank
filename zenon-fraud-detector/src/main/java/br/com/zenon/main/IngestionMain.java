package br.com.zenon.main;

import br.com.zenon.domain.Transaction;
import br.com.zenon.repository.TransactionSQLRepository;
import br.com.zenon.service.EfficientTransactionIngestor;
import br.com.zenon.service.TransactionIngestor;

import java.util.List;

public class IngestionMain {

    void main() {

        var repository = new TransactionSQLRepository();

        var transactionIngestor = new EfficientTransactionIngestor();

        long startTimeSQL = System.nanoTime();
        transactionIngestor.readAsBatch("data/PS_20174392719_1491204439457_log.csv",
                repository::saveAll);

        long endTimeSQL = System.nanoTime();

        long duracaoNanos = endTimeSQL - startTimeSQL;
        long duracaoSegundos = duracaoNanos / 1_000_000_000L;
        long horas = duracaoSegundos / 3600;
        long minutos = (duracaoSegundos % 3600) / 60;
        long segundos = duracaoSegundos % 60;
        long millis = (duracaoNanos / 1_000_000L) % 1000;

        String tempoFormatado = String.format("%02d:%02d:%02d.%03d", horas, minutos, segundos, millis);

        IO.println("Tempo de ingestão - SQL: " + tempoFormatado + " (hh:mm:ss.SSS)");
        IO.println("Tempo de ingestão - SQL (ms): " + duracaoNanos / 1_000_000.0);

    }

}