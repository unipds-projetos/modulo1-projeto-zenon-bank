package br.com.zenon;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class TransactionIngestor {

    public List<Transaction> readNew(String file) {

        try{
            List<String> lines = Files.readAllLines(Path.of(file));

            return lines.stream()
                    .skip(1)
                    .limit(1000)
                    .map(this::parseTransaction)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();

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
