package br.com.zenon;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.zenon.TransactionType.PAYMENT;

public class TransctionIngestor {

    private String arquivo;

    public String getArquivo() {return arquivo;}
    public void setArquivo(String arquivo) {this.arquivo = arquivo;}

    public List<Transaction> transctions(){
        Path path = Path.of(getArquivo());
        try(Stream<String> linhas = Files.lines(path)){
             List<Transaction> trans = linhas
                    // Filter and map lines to transactions
                    .skip(1)
                    .limit(2800)
                    .filter(linha -> !linha.isBlank())
                    .map(TransctionIngestor::criarItem)
                    .collect(Collectors.toList());
             return trans;
        }catch (IOException e){
            throw new RuntimeException(e);
        }

    }

    private static Transaction criarItem(String linha){
        String[] campos = linha.split(",");
        Transaction t = new Transaction(
                Integer.parseInt(campos[0]),
                TransactionType.valueOf(campos[1]),
                new BigDecimal(campos[2]),
                new TransactionCustumer(campos[3], new BigDecimal(campos[4]), new BigDecimal(campos[5])),
                new TransactionCustumer(campos[6], new BigDecimal(campos[7]), new BigDecimal(campos[8])),
                Boolean.parseBoolean(campos[9]),
                Boolean.parseBoolean(campos[10]));
        return t;

    }
}
