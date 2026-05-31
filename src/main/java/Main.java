import br.com.zenon.constants.FilePath;

import br.com.zenon.models.Transaction;
import br.com.zenon.utils.implementations.TransactionIngestor;

void main() throws IOException {
    TransactionIngestor ingestor = new TransactionIngestor(FilePath.CSV_FILE_PATH);
    List<Transaction> transactions = ingestor.getTransactions();
    transactions.forEach(IO::println);
}
