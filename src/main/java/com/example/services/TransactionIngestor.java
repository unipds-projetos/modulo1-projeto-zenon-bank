package com.example.services;

import com.example.dto.CustomerDTO;
import com.example.dto.TransactionDTO;
import com.example.enums.TransactionType;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class TransactionIngestor {

    public List<TransactionDTO> readTransactions(String fileName) {
        Path path = Path.of(fileName);
        try {
            List<String> lines = Files.readAllLines(path);
            return lines.stream()
                    .skip(1)
                    //.limit(100000)
                    .map(this::parseTransaction)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();

        } catch (Exception exception) {
            throw new RuntimeException("The file could not be found: " + fileName, exception);
        }
    }

    private Optional<TransactionDTO> parseTransaction(String line) {
        try {
            String[] chunks = line.split(",");

            int step = Integer.parseInt(chunks[0]);
            TransactionType type = TransactionType.valueOf(chunks[1]);


            BigDecimal amount = parseBigDecimal(chunks[2], "amount");

            var origin = new CustomerDTO(chunks[3],
                    parseBigDecimal(chunks[7], "oldBalanceOrig"),
                    parseBigDecimal(chunks[8], "newBalanceOrig"));

            var recipient = new CustomerDTO(chunks[6],
                    parseBigDecimal(chunks[7], "oldBalanceDest"),
                    parseBigDecimal(chunks[8], "newBalanceDest"));

            boolean isFraud = "1".equals(chunks[9]);
            boolean isFlaggedFraud = "1".equals(chunks[10]);

            return Optional.of(new TransactionDTO(step, type, amount, origin, recipient, isFraud, isFlaggedFraud));
        } catch (Exception e) {
            System.err.println("Erro ao fazer parse: " + line + " | " + e);
        }
        return Optional.empty();

    }

    private BigDecimal parseBigDecimal(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("O valor de " + fieldName + " não pode ser nulo nem vazio");
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("O valor de " + fieldName + " não é um número válido: " + value, e);
        }
    }
}
