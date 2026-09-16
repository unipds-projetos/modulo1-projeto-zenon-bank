package br.com.zenon;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class TransactionReport {

    private record ReportTransaction(BigDecimal amount, boolean isFraud){

    }

    public record Statistics(long totalTransactions, long totalFrauds, BigDecimal totalAmount){

    }

    public Statistics genererateReport(String filename){



        try{
            Stream<String> lines = Files.lines(Path.of(filename));

           return lines.skip(1)
                    .map(this::parseReportTransaction)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .reduce(
                            new Statistics(0, 0, BigDecimal.ZERO),
                            (Statistics acc, ReportTransaction rt)->{
                                return  new Statistics(
                                        acc.totalTransactions()+1,
                                        acc.totalFrauds() + (rt.isFraud() ? 1 : 0),
                                        acc.totalAmount.add(rt.amount));
                            }, (s1, s2) -> s1);




        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o arquivo: "+filename, e);
        }


    }

    private Optional<ReportTransaction> parseReportTransaction(String line){
        try{

            String [] chunks = line.split(",");




            BigDecimal amount = new BigDecimal(chunks[2]);




            boolean isFraud = "1".equals(chunks[9]);


            return Optional.of(new ReportTransaction(amount, isFraud));
        } catch (Exception e) {
            System.err.println("Erro ao fazer o parseTransaction: "+ line+ " - "+ e.getMessage());

            return Optional.empty();
        }

    }
}
