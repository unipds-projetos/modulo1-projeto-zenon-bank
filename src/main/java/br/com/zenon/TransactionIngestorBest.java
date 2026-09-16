package br.com.zenon;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class TransactionIngestorBest {

    public static final int FRAUD_LIMIT = 10_000;
    public static final int LINE_BATCH_LIMIT = 2_500;
    private final Semaphore dbPermits = new Semaphore(10);

    private void executeBatch(List<String> lineBatch, Consumer<List<Transaction>> BatchConsumer){

       List<Transaction> transactionBatch = lineBatch
               .stream()
               .map(this::parseTransaction)
               .filter(Optional::isPresent)
               .map(Optional::get)
               .toList();

       try{
           dbPermits.acquire();

           try {
               BatchConsumer.accept(transactionBatch);
           }finally {
               dbPermits.release();
           }
       } catch (InterruptedException e) {
           Thread.currentThread().interrupt();
       }
    }

    public void readAsBatch(String file, Consumer<List<Transaction>> BatchConsumer) {

        Path path = Path.of(file);
        try(ExecutorService executors = Executors.newVirtualThreadPerTaskExecutor();
                Stream<String> lines =  Files.lines(path).skip(1)){



           var iterator =  lines.iterator();


           List<String> lineBatch = new ArrayList<>(LINE_BATCH_LIMIT);

           while (iterator.hasNext()){
               String line = iterator.next();
               lineBatch.add(line);



               if(lineBatch.size() >= LINE_BATCH_LIMIT){
                   System.out.println("executando batch limit..."+LINE_BATCH_LIMIT);

                   final List<String> currentLineBatch = List.copyOf(lineBatch);
                   executors.submit(() -> {
                       try {

                           executeBatch(currentLineBatch, BatchConsumer);
                       } catch (Exception e) {
                           e.printStackTrace();
                       }
                   });

                   lineBatch.clear();
               }

           }
           if(!lineBatch.isEmpty()){
               System.out.println("executando batch final...");

               final List<String> currentLineBatch = List.copyOf(lineBatch);
               executors.submit(() -> {
                   try {

                       executeBatch(currentLineBatch, BatchConsumer);
                   } catch (Exception e) {
                       e.printStackTrace();
                   }
               });
           }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o arquivo"+e);
        }



    }

    public void readNew(String file, Consumer<Transaction> consumer) {

        Path path = Path.of(file);
        try(Stream<String> lines =  Files.lines(path)){


            lines
                    .skip(1)
//                    .limit(FRAUD_LIMIT)
                    .map(this::parseTransaction)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(consumer);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o arquivo"+e);
        }



    }

    public List<Optional<Transaction>> readOld(String filename){

        List<Optional<Transaction>> transactions = new ArrayList<>();

        try(FileInputStream fis = new FileInputStream(filename);
            Scanner sc = new Scanner(fis)){
            int lineCount = 0;


            while (sc.hasNextLine()){
                String line = sc.nextLine();
                lineCount++;



                if(lineCount == 1){
                    continue;
                }

                if(lineCount > 1001){
                    break;
                }

                Optional<Transaction> transaction = parseTransaction(line);
                transactions.add(transaction);

            }

        } catch (Exception e) {
            throw new RuntimeException("Error to read file",e);
        }

        return  transactions;


    }

    private Optional<Transaction> parseTransaction(String line) {

        try{

            String [] chunks = line.split(",");


            int step = Integer.parseInt(chunks[0]);


            TransactionType type = TransactionType.valueOf(chunks[1]);




            BigDecimal amount = new BigDecimal(chunks[2]);

            TransactionCostumer origin = new TransactionCostumer(chunks[3], new BigDecimal(chunks[4]), new BigDecimal(chunks[5]));

            TransactionCostumer recipient = new TransactionCostumer(chunks[6], new BigDecimal(chunks[7]), new BigDecimal(chunks[8]));


            boolean isFraud = "1".equals(chunks[9]);

            boolean isFlaggedFraud = "1".equals(chunks[10]);

            Transaction transaction = new Transaction(step, type,amount, origin, recipient, isFraud, isFlaggedFraud );
            return Optional.of(transaction);
        } catch (Exception e) {
            System.err.println("Erro ao fazer o parseTransaction: "+ line+ " - "+ e.getMessage());

            return Optional.empty();
        }
    }
}
