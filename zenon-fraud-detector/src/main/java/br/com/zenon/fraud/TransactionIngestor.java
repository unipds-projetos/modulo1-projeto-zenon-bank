package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TransactionIngestor {
    public List<Transaction> read(String fileName) {
        Path path = Path.of(fileName);
        try {
            List<String> lines = Files.readAllLines(path);
            return lines.stream()
                    .skip(1)
                    .limit(1000)
                    .map(this::parseTransaction)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("< Error >: ", e);
        }
    }

    private Optional<Transaction> parseTransaction(String line) {
        try {
            String[] chunks = line.split(",");
            if(chunks[2] == null || chunks[2].trim().isEmpty()) throw new IllegalArgumentException("The value of 'amount' cannot be null");
            if(chunks[4] == null || chunks[4].trim().isEmpty()) throw new IllegalArgumentException("The value of 'oldBalance' cannot be null");
            if(chunks[5] == null || chunks[5].trim().isEmpty()) throw new IllegalArgumentException("The value of 'newBalance' cannot be null");
            if(chunks[7] == null || chunks[7].trim().isEmpty()) throw new IllegalArgumentException("The value of 'oldBalance' cannot be null");
            if(chunks[8] == null || chunks[8].trim().isEmpty()) throw new IllegalArgumentException("The value of 'newBalance' cannot be null");

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
