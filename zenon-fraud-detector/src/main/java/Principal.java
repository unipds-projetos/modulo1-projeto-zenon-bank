import br.com.zenon.fraud.Transaction;
import br.com.zenon.fraud.TransactionIngestor;

import java.util.List;

class Principal {
    public static void main(String[] args) {
        System.out.println("Iniciando....");
        TransactionIngestor transactionIngestor = new TransactionIngestor();
        List<Transaction> lista = transactionIngestor.read("data/PS_20174392719_1491204439457_log.csv");
        lista.forEach(System.out::println);
    }
}
