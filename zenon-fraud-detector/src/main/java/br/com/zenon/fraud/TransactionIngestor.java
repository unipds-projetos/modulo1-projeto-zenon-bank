package br.com.zenon.fraud;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionIngestor {

    private static final int MAX_TRANSACTIONS = 1000;

    public List<Transaction> read(String fileName) {
        File file = resolveFile(fileName);
        List<Transaction> transactions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null && transactions.size() < MAX_TRANSACTIONS) {
                if (line.isBlank()) {
                    continue;
                }
                parseLine(line).ifPresent(transactions::add);
            }
        } catch (IOException e) {
            throw new RuntimeException("Falha ao ler o arquivo: " + file.getAbsolutePath(), e);
        }

        return transactions;
    }

    private File resolveFile(String fileName) {
        File file = new File(fileName);
        if (file.isFile()) {
            return file;
        }

        File fromModuleDirectory = new File("..", fileName);
        if (fromModuleDirectory.isFile()) {
            return fromModuleDirectory;
        }

        throw new IllegalArgumentException("Arquivo nao encontrado: " + fileName);
    }

    private Optional<Transaction> parseLine(String line) {
        try {
            String[] columns = line.split(",", -1);

            Transaction transaction = new Transaction(
                    Integer.parseInt(columns[0]),
                    TransactionType.valueOf(columns[1]),
                    new BigDecimal(columns[2]),
                    new TransactionCustomer(
                            columns[3],
                            new BigDecimal(columns[4]),
                            new BigDecimal(columns[5])
                    ),
                    new TransactionCustomer(
                            columns[6],
                            new BigDecimal(columns[7]),
                            new BigDecimal(columns[8])
                    ),
                    "1".equals(columns[9]),
                    "1".equals(columns[10])
            );
            return Optional.of(transaction);
        } catch (RuntimeException e) {
            System.err.println("Erro: " + line + " | " + e);
            return Optional.empty();
        }
    }
}
