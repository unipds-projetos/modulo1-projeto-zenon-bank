package br.com.zenon.fraud;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FraudAnalyzerTest {

    @Test
    void summarizesOnlyConfirmedFrauds() {
        FraudAnalyzer analyzer = new FraudAnalyzer(List.of(
                Fixtures.legitimate("C1", "5000.00"),
                Fixtures.fraud("C2", "200.00"),
                Fixtures.transaction(2, TransactionType.PAYMENT, "1200.00", "C3", true, true),
                Fixtures.transaction(2, TransactionType.TRANSFER, "300000.00", "C4", false, true),
                Fixtures.fraud("C2", "50.00")));

        assertEquals(3, analyzer.fraudCount());
        assertEquals(new BigDecimal("1450.00"), analyzer.totalLoss());
        assertEquals(
                List.of(new BigDecimal("1200.00"), new BigDecimal("200.00")),
                analyzer.largestFraudAmounts(2));
        assertEquals(List.of("C3", "C2"), analyzer.largestSuspiciousCustomers(2));
        assertEquals(
                Map.of(TransactionType.TRANSFER, 2L, TransactionType.PAYMENT, 1L),
                analyzer.fraudsByType());
    }

    @Test
    void returnsEveryFraudWhenTheLimitIsAboveTheResultSize() {
        FraudAnalyzer analyzer = new FraudAnalyzer(List.of(
                Fixtures.fraud("C1", "10.00"),
                Fixtures.legitimate("C2", "999.00")));

        assertEquals(List.of(new BigDecimal("10.00")), analyzer.largestFraudAmounts(5));
        assertEquals(List.of("C1"), analyzer.largestSuspiciousCustomers(5));
    }

    @Test
    void keepsTheOriginalOrderWhenFraudAmountsTie() {
        FraudAnalyzer analyzer = new FraudAnalyzer(List.of(
                Fixtures.fraud("C100", "10.00"),
                Fixtures.fraud("C200", "10.00")));

        assertEquals(List.of("C100", "C200"), analyzer.largestSuspiciousCustomers(2));
        assertEquals(
                List.of(new BigDecimal("10.00"), new BigDecimal("10.00")),
                analyzer.largestFraudAmounts(2));
    }

    @Test
    void reportsZeroWhenThereIsNoFraud() {
        FraudAnalyzer analyzer = new FraudAnalyzer(List.of(Fixtures.legitimate("C1", "10.00")));

        assertEquals(0, analyzer.fraudCount());
        assertEquals(BigDecimal.ZERO, analyzer.totalLoss());
        assertEquals(List.of(), analyzer.largestFraudAmounts(3));
        assertEquals(List.of(), analyzer.largestSuspiciousCustomers(3));
        assertEquals(Map.of(), analyzer.fraudsByType());
    }

    @Test
    void reportsZeroForAnEmptyBatch() {
        FraudAnalyzer analyzer = new FraudAnalyzer(List.of());

        assertEquals(0, analyzer.fraudCount());
        assertEquals(BigDecimal.ZERO, analyzer.totalLoss());
        assertTrue(analyzer.fraudsByType().isEmpty());
    }

    @Test
    void ignoresLaterChangesToTheSourceList() {
        ArrayList<Transaction> source = new ArrayList<>();
        source.add(Fixtures.fraud("C1", "10.00"));
        FraudAnalyzer analyzer = new FraudAnalyzer(source);

        source.clear();
        source.add(Fixtures.fraud("C2", "99.00"));

        assertEquals(1, analyzer.fraudCount());
        assertEquals(List.of("C1"), analyzer.largestSuspiciousCustomers(1));
    }

    @Test
    void rejectsANullSourceAndANegativeLimit() {
        assertEquals("transactions", assertThrows(NullPointerException.class, () -> new FraudAnalyzer(null)).getMessage());

        FraudAnalyzer analyzer = new FraudAnalyzer(List.of(Fixtures.fraud("C1", "10.00")));
        assertThrows(IllegalArgumentException.class, () -> analyzer.largestFraudAmounts(-1));
        assertThrows(IllegalArgumentException.class, () -> analyzer.largestSuspiciousCustomers(-1));
        assertThrows(IllegalArgumentException.class, () -> analyzer.largestFraudAmounts(1_001));
        assertThrows(IllegalArgumentException.class, () -> analyzer.largestSuspiciousCustomers(1_001));
        assertEquals(List.of(), analyzer.largestFraudAmounts(0));
        assertEquals(List.of(), analyzer.largestSuspiciousCustomers(0));
    }
}
