package br.com.zenon.transactions;

import br.com.zenon.enums.TransactionCategory;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;
import java.util.List;

public class TransactionIngetor {

    public List<Transaction> read(String filename){

        List<Transaction> transactionList = new ArrayList<>();

        try(Stream<String> stream = Files.lines(Path.of(filename))) {
            transactionList = stream.skip(1).limit(1000).map(TransactionIngetor::parseToTransaction).toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return transactionList;
    };

   private static Transaction parseToTransaction(String line){
       String[] data = line.split(",");

       return new Transaction(
               Integer.parseInt(data[0]),
               TransactionCategory.valueOf(data[1]),
               new BigDecimal(data[2]),
               createCustomer(data, 3),
               createCustomer(data, 6),
               "1".equals(data[9]),
               "1".equals(data[10])
       );
   }

   private static TransactionCustumer createCustomer(String[] data, int startIndex){
       return new TransactionCustumer(
               data[startIndex],
               new BigDecimal(data[startIndex + 1]),
               new BigDecimal(data[startIndex + 2])
       );
   }
}
