package br.com.zenon.fraud;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class TransactionSQLRepositoryIT {

    @BeforeEach
    void requireMysql() {
        assumeTrue(MysqlAvailability.isUp());
    }

    @Test
    void storesABatchAndReturnsTheFirstRowOfTheOrigin() throws Exception {
        Transaction first = Fixtures.fraud("C1231006815", "10.50");
        Transaction duplicate = Fixtures.legitimate("C1231006815", "99.00");
        Transaction other = Fixtures.transaction(2, TransactionType.CASH_OUT, "300.00", "C1000004", false, true);

        try (TransactionSQLRepository repository = TransactionSQLRepository.open()) {
            repository.deleteAll();
            repository.saveAll(List.of(first, duplicate, other));

            assertEquals(first, repository.findByOriginName("C1231006815").orElseThrow());
            assertEquals(other, repository.findByOriginName("C1000004").orElseThrow());
            assertTrue(repository.findByOriginName("C12345").isEmpty());
            assertTrue(repository.findByOriginName("C1' OR '1'='1").isEmpty());
            assertTrue(repository.findByOriginName(null).isEmpty());

            repository.save(Fixtures.legitimate("C42", "1.00"));
            assertEquals(new BigDecimal("1.00"), repository.findByOriginName("C42").orElseThrow().amount());

            repository.deleteAll();
            assertTrue(repository.findByOriginName("C1231006815").isEmpty());
        }
    }

    @Test
    void rejectsANullBatch() throws Exception {
        try (TransactionSQLRepository repository = TransactionSQLRepository.open()) {
            assertEquals(
                    "transactions",
                    assertThrows(NullPointerException.class, () -> repository.saveAll(null)).getMessage());
            assertEquals(
                    "transaction",
                    assertThrows(NullPointerException.class, () -> repository.save(null)).getMessage());
        }
    }
}
