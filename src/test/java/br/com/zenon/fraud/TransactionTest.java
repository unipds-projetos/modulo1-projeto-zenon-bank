package br.com.zenon.fraud;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionTest {

    @Test
    void keepsThePaySimColumns() {
        TransactionCustomer origin = Fixtures.customer("C100", "5000.00", "0.00");
        TransactionCustomer recipient = Fixtures.customer("M200", "0.00", "5000.00");

        Transaction transaction = new Transaction(
                1,
                TransactionType.PAYMENT,
                new BigDecimal("5000.00"),
                origin,
                recipient,
                true,
                false);

        assertEquals(1, transaction.step());
        assertEquals(TransactionType.PAYMENT, transaction.type());
        assertEquals(new BigDecimal("5000.00"), transaction.amount());
        assertEquals(origin, transaction.origin());
        assertEquals(recipient, transaction.recipient());
        assertTrue(transaction.isFraud());
        assertFalse(transaction.isFlaggedFraud());
    }

    @Test
    void acceptsTheStepBoundsAndAZeroAmount() {
        Transaction first = validTransaction(1, "0.00");
        Transaction last = validTransaction(10_000, "1000000000000.00");

        assertEquals(1, first.step());
        assertEquals(new BigDecimal("0.00"), first.amount());
        assertEquals(10_000, last.step());
        assertEquals(new BigDecimal("1000000000000.00"), last.amount());
    }

    @Test
    void rejectsAStepOutsideTheRange() {
        assertEquals(
                "step should be positive: 0",
                assertThrows(IllegalArgumentException.class, () -> validTransaction(0, "10")).getMessage());
        assertEquals(
                "step should be positive: -1",
                assertThrows(IllegalArgumentException.class, () -> validTransaction(-1, "10")).getMessage());
        assertEquals(
                "step is outside the allowed range",
                assertThrows(IllegalArgumentException.class, () -> validTransaction(10_001, "10")).getMessage());
    }

    @Test
    void rejectsAnAmountOutsideTheRange() {
        assertEquals(
                "amount should be positive: -50.00",
                assertThrows(IllegalArgumentException.class, () -> validTransaction(1, "-50.00")).getMessage());
        IllegalArgumentException scale = assertThrows(
                IllegalArgumentException.class, () -> validTransaction(1, "1.001"));
        IllegalArgumentException huge = assertThrows(
                IllegalArgumentException.class, () -> validTransaction(1, "1000000000000.01"));

        assertEquals("amount is outside the allowed range", scale.getMessage());
        assertEquals("amount is outside the allowed range", huge.getMessage());
        assertFalse(scale.getMessage().contains("1.001"));
    }

    @Test
    void rejectsNullParts() {
        TransactionCustomer origin = Fixtures.customer("C100", "1", "0");
        TransactionCustomer recipient = Fixtures.customer("C200", "0", "1");
        BigDecimal amount = new BigDecimal("10");

        assertEquals("type", assertThrows(NullPointerException.class,
                () -> new Transaction(1, null, amount, origin, recipient, false, false)).getMessage());
        assertEquals("amount", assertThrows(NullPointerException.class,
                () -> new Transaction(1, TransactionType.DEBIT, null, origin, recipient, false, false)).getMessage());
        assertEquals("origin", assertThrows(NullPointerException.class,
                () -> new Transaction(1, TransactionType.DEBIT, amount, null, recipient, false, false)).getMessage());
        assertEquals("recipient", assertThrows(NullPointerException.class,
                () -> new Transaction(1, TransactionType.DEBIT, amount, origin, null, false, false)).getMessage());
    }

    private static Transaction validTransaction(int step, String amount) {
        return new Transaction(
                step,
                TransactionType.PAYMENT,
                new BigDecimal(amount),
                Fixtures.customer("C100", "100", "0"),
                Fixtures.customer("M200", "0", "0"),
                false,
                false);
    }
}

