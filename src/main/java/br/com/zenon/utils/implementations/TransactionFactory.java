package br.com.zenon.utils.implementations;

import br.com.zenon.constants.DefaultColumns;
import br.com.zenon.models.FraudDemark;
import br.com.zenon.models.Origin;
import br.com.zenon.models.Transaction;
import br.com.zenon.models.types.TransactionType;

import java.math.BigDecimal;
import java.util.Map;

public class TransactionFactory {

    private final Map<String, Integer> columnIndexMap;

    public TransactionFactory(Map<String, Integer> columnIndexMap) {
        this.columnIndexMap = columnIndexMap;
    }

    public Transaction create(String[] row) {
        int step = Integer.parseInt(getValue(row, DefaultColumns.STEP));
        TransactionType type = TransactionType.valueOf(getValue(row, DefaultColumns.TYPE).toUpperCase());
        BigDecimal amount = new BigDecimal(getValue(row, DefaultColumns.AMOUNT));

        Origin origin = new Origin(
                getValue(row, DefaultColumns.ORIGIN_NAME),
                new BigDecimal(getValue(row, DefaultColumns.ORIGIN_OLD_BALANCE)),
                new BigDecimal(getValue(row, DefaultColumns.ORIGIN_NEW_BALANCE))
        );

        Origin destinatary = new Origin(
                getValue(row, DefaultColumns.DESTINATARY_NAME),
                new BigDecimal(getValue(row, DefaultColumns.DESTINATARY_OLD_BALANCE)),
                new BigDecimal(getValue(row, DefaultColumns.DESTINATARY_NEW_BALANCE))
        );

        boolean isFraud = "1".equals(getValue(row, DefaultColumns.FRAUD_INDICATOR));
        boolean isFlagged = "1".equals(getValue(row, DefaultColumns.FRAUD_FLAGGED));
        FraudDemark fraudDemark = new FraudDemark(isFraud, isFlagged);

        return new Transaction(step, type, amount, origin, destinatary, fraudDemark);
    }

    private String getValue(String[] row, String columnName) {
        Integer index = columnIndexMap.get(columnName);
        if (index == null || index >= row.length) {
            throw new IllegalArgumentException("Column not found or index out of bounds: " + columnName);
        }
        return row[index];
    }
}