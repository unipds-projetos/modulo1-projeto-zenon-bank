package br.com.zenon.utils.implementations;

import br.com.zenon.models.Transaction;
import br.com.zenon.utils.IMapper;
import br.com.zenon.utils.IReader;

import java.io.IOException;
import java.util.List;

public class TransactionIngestor {
    private IReader<Transaction> reader = new ReaderCSV<>();
    private IMapper<Transaction> mapper = new TransactionMapper();
    private List<Transaction> transactions;

    public TransactionIngestor(String fileName) throws IOException {
        transactions = reader.read(mapper, fileName, 1000);
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}
