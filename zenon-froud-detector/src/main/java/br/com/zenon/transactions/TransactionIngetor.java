package br.com.zenon.transactions;

import br.com.zenon.enums.TransactionCategory;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.List;

public class TransactionIngetor {

    public List<Transaction> read(String filename){

        List<Transaction> transactionList = new ArrayList<>();

        try(Stream<String> stream = Files.lines(Path.of(filename))) {
            transactionList = stream
                    .skip(1)
                    .limit(1000)
                    .map(this::parseToTransaction)
                    .filter(Optional::isPresent)
                    .map(Optional::get).toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return transactionList;
    };

   private Optional <Transaction> parseToTransaction(String line){

       try {
            String[] data = line.split(",");

            int step = Integer.parseInt(data[0]);
            TransactionCategory type = TransactionCategory.valueOf(data[1]);

            BigDecimal amount = new BigDecimal(data[2]);

            TransactionCustomer custumersDest = createCustomer(data, 3);
            TransactionCustomer custumerRecipient = createCustomer(data, 6);


            boolean isFraud = "1".equals(data[9]);
            boolean isFlaggedFraud = "1".equals(data[10]);

           return Optional.of( new Transaction(
                   step,
                   type,
                   amount,
                   custumersDest,
                   custumerRecipient,
                   isFraud,
                   isFlaggedFraud
           ));
       }catch (Exception e){
           System.err.println("Ao fazer parse: " + line + " | " + e );
           return Optional.empty();
       }
   }

   private static TransactionCustomer createCustomer(String[] data, int startIndex){
       String name = data[startIndex];
       BigDecimal oldBalance =  new BigDecimal(data[startIndex + 1]);
       BigDecimal newBalance = new BigDecimal(data[startIndex + 2]);

       return new TransactionCustomer(
               name,
               oldBalance,
               newBalance
       );
   }
}
