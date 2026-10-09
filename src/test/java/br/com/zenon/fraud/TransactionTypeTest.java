package br.com.zenon.fraud;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionTypeTest {

    @Test
    void listsThePaySimTypes() {
        assertArrayEquals(
                new TransactionType[] {
                        TransactionType.CASH_IN,
                        TransactionType.CASH_OUT,
                        TransactionType.DEBIT,
                        TransactionType.PAYMENT,
                        TransactionType.TRANSFER
                },
                TransactionType.values());
    }

    @Test
    void readsEachTypeByName() {
        assertEquals(TransactionType.CASH_OUT, TransactionType.valueOf("CASH_OUT"));
        assertEquals(TransactionType.TRANSFER, TransactionType.valueOf("TRANSFER"));
    }

    @Test
    void rejectsAnUnknownType() {
        assertThrows(IllegalArgumentException.class, () -> TransactionType.valueOf("COMPRA_INVALIDA"));
        assertThrows(IllegalArgumentException.class, () -> TransactionType.valueOf("payment"));
    }
}
