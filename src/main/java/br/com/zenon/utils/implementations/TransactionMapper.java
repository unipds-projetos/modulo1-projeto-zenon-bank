package br.com.zenon.utils.implementations;

import br.com.zenon.models.Transaction;
import br.com.zenon.utils.IMapper;

import java.util.HashMap;
import java.util.Map;

public class TransactionMapper implements IMapper<Transaction> {
    private TransactionFactory transactionFactory;

    @Override
    public void initializeHeaders(String[] headers) {
        Map<String, Integer> columnIndexMap = new HashMap<>(headers.length);
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i];
            columnIndexMap.put(header, i);
        }
        this.transactionFactory = new TransactionFactory(columnIndexMap);
    }

    @Override
    public Transaction parse(String[] values) {
        return transactionFactory.create(values);
    }
}
