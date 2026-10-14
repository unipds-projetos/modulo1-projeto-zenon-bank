package br.com.zenon.fraud;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionRepositoryTest {

    @ParameterizedTest
    @MethodSource("repositories")
    void findsTheFirstTransactionOfTheOrigin(TransactionRepository repository) {
        Optional<Transaction> first = repository.findByOriginName("C1");

        assertTrue(first.isPresent());
        assertEquals(new java.math.BigDecimal("10.00"), first.orElseThrow().amount());
        assertEquals("C2", repository.findByOriginName("C2").orElseThrow().origin().name());
    }

    @ParameterizedTest
    @MethodSource("repositories")
    void returnsEmptyWhenTheOriginIsUnknown(TransactionRepository repository) {
        assertTrue(repository.findByOriginName("C-MISSING").isEmpty());
        assertTrue(repository.findByOriginName(null).isEmpty());
        assertTrue(repository.findByOriginName("").isEmpty());
    }

    @Test
    void returnsEmptyForAnEmptySource() {
        assertTrue(new TransactionListRepository(List.of()).findByOriginName("C1").isEmpty());
        assertTrue(new TransactionMapRepository(List.of()).findByOriginName("C1").isEmpty());
    }

    @Test
    void keepsASnapshotAndTheFirstOccurrence() {
        ArrayList<Transaction> source = new ArrayList<>();
        source.add(Fixtures.fraud("C1", "10.00"));
        source.add(Fixtures.legitimate("C1", "99.00"));
        TransactionRepository listRepository = new TransactionListRepository(source);
        TransactionRepository mapRepository = new TransactionMapRepository(source);

        source.clear();

        assertEquals(new java.math.BigDecimal("10.00"), listRepository.findByOriginName("C1").orElseThrow().amount());
        assertEquals(new java.math.BigDecimal("10.00"), mapRepository.findByOriginName("C1").orElseThrow().amount());
        assertEquals(
                listRepository.findByOriginName("C1").orElseThrow(),
                mapRepository.findByOriginName("C1").orElseThrow());
    }

    @ParameterizedTest
    @MethodSource("repositories")
    void savesANewOriginWithoutReplacingTheFirstOne(TransactionRepository repository) {
        repository.save(Fixtures.legitimate("C3", "7.00"));
        repository.save(Fixtures.legitimate("C1", "1.00"));

        assertEquals(new java.math.BigDecimal("7.00"), repository.findByOriginName("C3").orElseThrow().amount());
        assertEquals(new java.math.BigDecimal("10.00"), repository.findByOriginName("C1").orElseThrow().amount());
    }

    @ParameterizedTest
    @MethodSource("repositories")
    void rejectsANullTransaction(TransactionRepository repository) {
        assertEquals(
                "transaction",
                assertThrows(NullPointerException.class, () -> repository.save(null)).getMessage());
    }

    @Test
    void rejectsANullSource() {
        assertEquals(
                "transactions",
                assertThrows(NullPointerException.class, () -> new TransactionListRepository(null)).getMessage());
        assertEquals(
                "transactions",
                assertThrows(NullPointerException.class, () -> new TransactionMapRepository(null)).getMessage());
    }

    static Stream<TransactionRepository> repositories() {
        List<Transaction> transactions = List.of(
                Fixtures.fraud("C1", "10.00"),
                Fixtures.legitimate("C1", "99.00"),
                Fixtures.legitimate("C2", "5.00"));
        return Stream.of(
                new TransactionListRepository(transactions),
                new TransactionMapRepository(transactions));
    }
}
