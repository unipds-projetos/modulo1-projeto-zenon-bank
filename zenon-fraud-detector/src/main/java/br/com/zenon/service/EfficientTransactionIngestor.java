package br.com.zenon.service;

import br.com.zenon.domain.Transaction;
import br.com.zenon.domain.TransactionCustumer;
import br.com.zenon.domain.TransactionType;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Stream;


public class EfficientTransactionIngestor {

    public static final int FRAUD_LIMIT = 10_000;
    public static final int LINE_BATCH_SIZE = 2_500;

    public void readAsBatch(String filename, Consumer<List<Transaction>> batchConsumer){
        Path path = Path.of(filename);

        try(ExecutorService executor = Executors.newFixedThreadPool(10);Stream<String> lines = Files.lines(path).skip(1))
            {


            var iterator = lines.iterator();
           // if (iterator.hasNext()) iterator.next();

            List<String> linesBatch = new ArrayList<>();
            while (iterator.hasNext()) {

                String line = iterator.next();
                linesBatch.add(line);


                if (linesBatch.size() >= LINE_BATCH_SIZE){
                    IO.println("EXECUTANDO BATCH INGESTOR");
                    final List<String> currentLinesBatch = List.copyOf(linesBatch);
                    executor.submit(() -> executeBatch(currentLinesBatch, batchConsumer));
                    linesBatch.clear();
                }
            }

            if (!linesBatch.isEmpty()){
                IO.println("EXECUTANDO BATCH FINAL");
                final List<String> currentLinesBatch = List.copyOf(linesBatch);
                executor.submit(() -> executeBatch(currentLinesBatch, batchConsumer));
            }

        }catch (Exception ex){
            throw new RuntimeException("Erro ao ler o arquivo: " + filename, ex);
        }

    }

    private void executeBatch(List<String> linesBatch, Consumer<List<Transaction>> batchConsumer) {
        List<Transaction> transactionBatch = linesBatch.stream()
                .map(this::parseTransaction)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        batchConsumer.accept(transactionBatch);
    }

    public void readAsStream(String filename, Consumer<Transaction> consumer) {
        Path path = Path.of(filename);
        try (Stream<String> lines = Files.lines(path)){

             lines
                    .skip(1)
                    //.limit(FRAUD_LIMIT)
                    .map(this::parseTransaction)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(consumer);
        } catch (Exception ex) {
            throw new RuntimeException("Erro ao ler o arquivo: " + filename, ex);
        }

    }

    private Optional<Transaction> parseTransaction(String line) {
        try {
            String[] chuncks = line.split(",");

            TransactionType type = TransactionType.valueOf(chuncks[1]);
            if(chuncks[2] == null || chuncks[2].trim().isEmpty()){
                throw new IllegalArgumentException("O valor de amount não pode ser nulo ou vazio");
            }
            BigDecimal amount = new BigDecimal(chuncks[2]);
            TransactionCustumer origin = new TransactionCustumer(chuncks[3], new BigDecimal(chuncks[4]), new BigDecimal(chuncks[5]));
            TransactionCustumer recipient = new TransactionCustumer(chuncks[6], new BigDecimal(chuncks[7]), new BigDecimal(chuncks[8]));

            int step = Integer.parseInt(chuncks[0]);

            boolean isFraud = chuncks[9].equals("1");
            boolean isFlaggedFraud = chuncks[10].equals("1");

            return Optional.of(new Transaction(step, type, amount, origin, recipient, isFraud, isFlaggedFraud));
        }catch (Exception ex){
            System.err.println("Erro ao fazer parse: " + line + " - " + ex.getMessage());
        }
        return Optional.empty();
    }
}
