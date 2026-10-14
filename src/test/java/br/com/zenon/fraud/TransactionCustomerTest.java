package br.com.zenon.fraud;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionCustomerTest {

    @Test
    void keepsNameAndBalances() {
        TransactionCustomer customer = Fixtures.customer("C100", "10.00", "0.50");

        assertEquals("C100", customer.name());
        assertEquals(new BigDecimal("10.00"), customer.oldBalance());
        assertEquals(new BigDecimal("0.50"), customer.newBalance());
    }

    @Test
    void acceptsZeroBalances() {
        TransactionCustomer customer = Fixtures.customer("C100", "0.00", "0");

        assertEquals(new BigDecimal("0.00"), customer.oldBalance());
        assertEquals(new BigDecimal("0"), customer.newBalance());
    }

    @Test
    void rejectsMissingName() {
        assertEquals(
                "name should not be empty",
                assertThrows(IllegalArgumentException.class, () -> Fixtures.customer(null, "1", "1")).getMessage());
        assertEquals(
                "name should not be empty",
                assertThrows(IllegalArgumentException.class, () -> Fixtures.customer("", "1", "1")).getMessage());
        assertEquals(
                "name should not be empty",
                assertThrows(IllegalArgumentException.class, () -> Fixtures.customer("   ", "1", "1")).getMessage());
    }

    @Test
    void rejectsBalancesOutsideTheRangeWithoutEchoingThem() {
        IllegalArgumentException oldBalance = assertThrows(
                IllegalArgumentException.class, () -> Fixtures.customer("C100", "-0.01", "0"));
        IllegalArgumentException newBalance = assertThrows(
                IllegalArgumentException.class, () -> Fixtures.customer("C100", "0", "-1"));
        IllegalArgumentException scale = assertThrows(
                IllegalArgumentException.class, () -> Fixtures.customer("C100", "1.001", "0"));

        assertEquals("oldBalance should be positive: -0.01", oldBalance.getMessage());
        assertEquals("newBalance should be positive: -1", newBalance.getMessage());
        assertEquals("oldBalance is outside the allowed range", scale.getMessage());
        assertFalse(scale.getMessage().contains("1.001"));
    }

    @Test
    void rejectsANameOutsideThePaySimFormat() {
        assertEquals(
                "name has an invalid format",
                assertThrows(IllegalArgumentException.class, () -> Fixtures.customer("M-DEST", "1", "0")).getMessage());
        assertEquals(
                "name has an invalid format",
                assertThrows(IllegalArgumentException.class, () -> Fixtures.customer("c100", "1", "0")).getMessage());
        assertEquals(
                "name has an invalid format",
                assertThrows(IllegalArgumentException.class, () -> Fixtures.customer("../C1", "1", "0")).getMessage());
        assertEquals(
                "C" + "1".repeat(20),
                Fixtures.customer("C" + "1".repeat(20), "1", "0").name());
        assertThrows(IllegalArgumentException.class, () -> Fixtures.customer("C" + "1".repeat(21), "1", "0"));
    }

    @Test
    void rejectsNullBalances() {
        assertEquals(
                "oldBalance",
                assertThrows(NullPointerException.class,
                        () -> new TransactionCustomer("C100", null, BigDecimal.ZERO)).getMessage());
        assertEquals(
                "newBalance",
                assertThrows(NullPointerException.class,
                        () -> new TransactionCustomer("C100", BigDecimal.ZERO, null)).getMessage());
    }
}
