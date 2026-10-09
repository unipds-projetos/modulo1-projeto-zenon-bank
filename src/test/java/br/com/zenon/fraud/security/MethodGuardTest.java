package br.com.zenon.fraud.security;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MethodGuardTest {

    @Test
    void checksNullCustomerNameMoneyStepAndLimit() {
        assertEquals("type", assertThrows(NullPointerException.class,
                () -> MethodGuard.notNull(null, "type")).getMessage());
        assertEquals("C10", MethodGuard.customerName("C10"));
        assertEquals(
                "name should not be empty",
                assertThrows(IllegalArgumentException.class, () -> MethodGuard.customerName("  ")).getMessage());
        assertEquals(
                "name has an invalid format",
                assertThrows(IllegalArgumentException.class, () -> MethodGuard.customerName("cliente")).getMessage());

        assertEquals(new BigDecimal("10.00"), MethodGuard.money(new BigDecimal("10.00"), "amount"));
        assertEquals(new BigDecimal("100"), MethodGuard.money(new BigDecimal("1E+2"), "amount"));
        assertEquals(new BigDecimal("10000000"), MethodGuard.money(new BigDecimal("1.0E7"), "amount"));
        assertEquals(
                "amount is outside the allowed range",
                assertThrows(IllegalArgumentException.class,
                        () -> MethodGuard.money(new BigDecimal("1E+20"), "amount")).getMessage());

        assertEquals(1, MethodGuard.step(1));
        assertEquals(10_000, MethodGuard.step(10_000));
        assertEquals(
                "step is outside the allowed range",
                assertThrows(IllegalArgumentException.class, () -> MethodGuard.step(10_001)).getMessage());

        assertEquals(0, MethodGuard.limit(0));
        assertEquals(1_000, MethodGuard.limit(1_000));
        assertEquals(
                "limit is outside the allowed range",
                assertThrows(IllegalArgumentException.class, () -> MethodGuard.limit(-1)).getMessage());
        assertEquals(
                "limit is outside the allowed range",
                assertThrows(IllegalArgumentException.class, () -> MethodGuard.limit(1_001)).getMessage());

        assertEquals(Optional.of("C10"), MethodGuard.customerQuery("C10"));
        assertTrue(MethodGuard.customerQuery(null).isEmpty());
        assertTrue(MethodGuard.customerQuery("").isEmpty());
        assertTrue(MethodGuard.customerQuery("cliente").isEmpty());
    }

    @Test
    void acceptsOnlyBinaryFlagsAndAnEmptyArgumentList() {
        assertFalse(MethodGuard.fraudFlag("0"));
        assertTrue(MethodGuard.fraudFlag("1"));
        assertEquals(
                "flag should be 0 or 1",
                assertThrows(IllegalArgumentException.class, () -> MethodGuard.fraudFlag("2")).getMessage());
        assertThrows(NullPointerException.class, () -> MethodGuard.fraudFlag(null));

        MethodGuard.requireNoArguments(new String[0]);
        assertEquals(
                "this entry point does not accept arguments",
                assertThrows(IllegalArgumentException.class, () -> MethodGuard.requireNoArguments(null)).getMessage());
        assertEquals(
                "this entry point does not accept arguments",
                assertThrows(IllegalArgumentException.class,
                        () -> MethodGuard.requireNoArguments(new String[] {"arquivo.csv"})).getMessage());
    }

    @Test
    void describesARejectedRowByLengthOnly() {
        String disclosed = MethodGuard.rejectedRow("segredo,PAYMENT,-1");

        assertEquals("linha rejeitada (18 caracteres)", disclosed);
        assertFalse(disclosed.contains("segredo"));
        assertEquals("linha rejeitada (0 caracteres)", MethodGuard.rejectedRow(null));
    }

    @Test
    void resolvesACsvInsideDataAndRejectsASymbolicLink() throws IOException {
        Files.createDirectories(Path.of("data"));
        Path csv = Path.of("data", "zz-test-guard.csv");
        Path link = Path.of("data", "zz-test-guard-link.csv");
        Files.writeString(csv, "step\n");
        try {
            assertEquals(csv.toAbsolutePath().normalize(), MethodGuard.dataFile("data/zz-test-guard.csv"));

            Files.createSymbolicLink(link, csv.getFileName());
            assertEquals(
                    "symbolic links are not allowed",
                    assertThrows(IllegalArgumentException.class,
                            () -> MethodGuard.dataFile("data/zz-test-guard-link.csv")).getMessage());
        } finally {
            Files.deleteIfExists(link);
            Files.deleteIfExists(csv);
        }
    }
}
