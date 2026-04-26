import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class TransactionIngestor {
    public static List<Transaction> read(String fileName) throws IOException {
        Path path = Paths.get(fileName);

        try (Stream<String> lines = Files.lines(path)) {
            return lines
                    .skip(1) // ignora cabeçalho
                    .limit(1000) // lê apenas as primeiras 1000 linhas
                    .map(line -> {
                        try {
                            String[] parts = line.split(",");

                            int step = Integer.parseInt(parts[0]);
                            TransactionType type = TransactionType.valueOf(parts[1].toUpperCase());
                            BigDecimal amount = new BigDecimal(parts[2]);

                            Customer origin = new Customer(parts[3],
                                    new BigDecimal(parts[4]),
                                    new BigDecimal(parts[5]));

                            Customer destination = new Customer(parts[6],
                                    new BigDecimal(parts[7]),
                                    new BigDecimal(parts[8]));

                            boolean isFraud = parts[parts.length - 2].equals("1");
                            boolean isFlaggedFraud = parts[parts.length - 1].equals("1");

                            return Optional.of(new Transaction(step, type, amount, origin, destination, isFraud, isFlaggedFraud));

                        } catch (Exception e) {
                            System.err.println("Erro: " + line + " | " + e);
                            return Optional.<Transaction>empty();
                        }
                    })
                    .flatMap(Optional::stream)
                    .collect(Collectors.toList());
        }
    }
}