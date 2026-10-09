package br.com.zenon.fraud;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TransactionIngestor {

    private static final int MAX_LINES = 1000;

    public List<Transaction> ingest(String fileName) throws IOException {
        List<Transaction> transactions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String header = reader.readLine();
            if (header == null) {
                return transactions;
            }

            String line;
            int read = 0;
            while (read < MAX_LINES && (line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                transactions.add(parse(line));
                read++;
            }
        }

        return transactions;
    }

    private Transaction parse(String line) {
        String[] columns = line.split(",");
        if (columns.length != 11) {
            throw new IllegalArgumentException("Linha CSV com " + columns.length + " colunas: " + line);
        }

        return new Transaction(
                Integer.parseInt(columns[0]),
                TransactionType.valueOf(columns[1].replace('-', '_')),
                new BigDecimal(columns[2]),
                new TransactionCustomer(columns[3], new BigDecimal(columns[4]), new BigDecimal(columns[5])),
                new TransactionCustomer(columns[6], new BigDecimal(columns[7]), new BigDecimal(columns[8])),
                flag(columns[9]),
                flag(columns[10]));
    }

    private boolean flag(String value) {
        return "1".equals(value);
    }
}
