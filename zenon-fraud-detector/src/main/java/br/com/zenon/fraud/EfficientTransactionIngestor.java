package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class EfficientTransactionIngestor {
    //public static final int FRAUD_LIMIT = 10_000;
    public static final int LINE_BATCH_SIZE = 2_500;

    private final Semaphore dbPermits = new Semaphore(10);

    public void readAsStream(String fileName, Consumer<Transaction> consumer) {
        Path path = Path.of(fileName);
        try (Stream<String> lines = Files.lines(path)) {
            lines
                    .skip(1)
                    //.limit(FRAUD_LIMIT)
                    .map(this::parseTransaction)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(consumer);
        } catch (Exception e) {
            throw new RuntimeException("< Error >: ", e);
        }
    }

    public void readAsBatch(String fileName, Consumer<List<Transaction>> batchConsumer) {
        Path path = Path.of(fileName);
        try (
                ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
                Stream<String> lines = Files.lines(path).skip(1);
        ) {
            var iterator = lines.iterator();
            List<String> lineBatch = new ArrayList<>(LINE_BATCH_SIZE);

            while(iterator.hasNext()){
                String line = iterator.next();
                lineBatch.add(line);

                if(lineBatch.size() >= LINE_BATCH_SIZE){
                    IO.println("Executing batch ingestor...");
                    final List<String> currentLineBatch = List.copyOf(lineBatch);
                    executor.submit( () -> {
                        try {
                            executeBatch(currentLineBatch, batchConsumer);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    lineBatch.clear();
                }
            }
            if(!lineBatch.isEmpty()){
                IO.println("Executing final batch");
                final List<String> currentLineBatch = List.copyOf(lineBatch);
                executor.submit( () -> {
                    try {
                        executeBatch(currentLineBatch, batchConsumer);
                    } catch (Exception e) {
                        e.printStackTrace(); // TODO: criar logger
                    }
                });
                lineBatch.clear();
            }
        } catch (Exception e) {
            throw new RuntimeException("< Error reading file >: " + fileName + ". ", e);
        }
    }

    private void executeBatch(List<String> lineBatch, Consumer<List<Transaction>> batchConsumer) {
        List<Transaction> transactionBatch = lineBatch
                .stream()
                .map(this::parseTransaction)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        try{
            dbPermits.acquire();
            try {
                batchConsumer.accept(transactionBatch);
            } finally {
                dbPermits.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private Optional<Transaction> parseTransaction(String line) {
        try {
            String[] chunks = line.split(",");
            if (chunks[2] == null || chunks[2].trim().isEmpty())
                throw new IllegalArgumentException("The value of 'amount' cannot be null");
            if (chunks[4] == null || chunks[4].trim().isEmpty())
                throw new IllegalArgumentException("The value of 'oldBalance' cannot be null");
            if (chunks[5] == null || chunks[5].trim().isEmpty())
                throw new IllegalArgumentException("The value of 'newBalance' cannot be null");
            if (chunks[7] == null || chunks[7].trim().isEmpty())
                throw new IllegalArgumentException("The value of 'oldBalance' cannot be null");
            if (chunks[8] == null || chunks[8].trim().isEmpty())
                throw new IllegalArgumentException("The value of 'newBalance' cannot be null");

            int step = Integer.parseInt(chunks[0]);
            TransactionType type = TransactionType.valueOf(chunks[1]);

            BigDecimal amount = new BigDecimal(chunks[2]);

            var origin = new TransactionCustomer(chunks[3], new BigDecimal(chunks[4]), new BigDecimal(chunks[5]));
            var recipient = new TransactionCustomer(chunks[6], new BigDecimal(chunks[7]), new BigDecimal(chunks[8]));
            boolean isFraud = "1".equals(chunks[9]);
            boolean isFlaggedFraud = "1".equals(chunks[10]);

            return Optional.of(new Transaction(step, type, amount, origin, recipient, isFraud, isFlaggedFraud));

        } catch (Exception e) {
            System.err.println("Error at parse: " + line + " | " + e);
            return Optional.empty();
        }
    }
}
