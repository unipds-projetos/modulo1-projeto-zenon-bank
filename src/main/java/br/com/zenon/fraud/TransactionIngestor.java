package br.com.zenon.fraud;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import br.com.zenon.fraud.security.MethodGuard;

public class TransactionIngestor {

    private static final int MAX_LINES = 100_000;
    private static final int MAX_LINE_LENGTH = 512;
    private static final String HEADER = "step,type,amount,nameOrig,oldbalanceOrg,newbalanceOrig,"
            + "nameDest,oldbalanceDest,newbalanceDest,isFraud,isFlaggedFraud";

    public List<Transaction> ingest(String fileName) throws IOException {
        return ingest(fileName, MAX_LINES);
    }

    public List<Transaction> ingest(String fileName, int maxLines) throws IOException {
        if (maxLines < 1 || maxLines > MAX_LINES) {
            throw new IllegalArgumentException("line limit is outside the allowed range");
        }
        Path source = MethodGuard.dataFile(fileName);
        List<Transaction> transactions = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(source, StandardCharsets.UTF_8)) {
            String header = reader.readLine();
            if (!HEADER.equals(header)) {
                throw new IllegalArgumentException("csv header is not the PaySim schema");
            }

            String line;
            int read = 0;
            while (read < maxLines && (line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                if (line.length() > MAX_LINE_LENGTH) {
                    System.err.println("Erro: " + MethodGuard.rejectedRow(line));
                    read++;
                    continue;
                }
                try {
                    transactions.add(parse(line));
                } catch (IllegalArgumentException error) {
                    System.err.println("Erro: " + MethodGuard.rejectedRow(line) + " | " + error.getClass().getSimpleName());
                }
                read++;
            }
        }

        return List.copyOf(transactions);
    }

    private Transaction parse(String line) {
        String[] columns = line.split(",", -1);
        if (columns.length != 11) {
            throw new IllegalArgumentException("csv row should have 11 colunas");
        }

        return new Transaction(
                Integer.parseInt(columns[0].trim()),
                TransactionType.valueOf(columns[1].trim().replace('-', '_')),
                new BigDecimal(columns[2].trim()),
                new TransactionCustomer(columns[3].trim(), new BigDecimal(columns[4].trim()), new BigDecimal(columns[5].trim())),
                new TransactionCustomer(columns[6].trim(), new BigDecimal(columns[7].trim()), new BigDecimal(columns[8].trim())),
                MethodGuard.fraudFlag(columns[9].trim()),
                MethodGuard.fraudFlag(columns[10].trim()));
    }
}
