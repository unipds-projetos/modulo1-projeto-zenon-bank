package br.com.zenon.fraud;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

final class InsertConnections implements AutoCloseable {

    private final TransactionSQLRepository[] repositories;
    private final ArrayBlockingQueue<TransactionSQLRepository> idle;

    static InsertConnections open(int size) throws SQLException {
        if (size < 1) {
            throw new IllegalArgumentException("thread count is outside the allowed range");
        }
        TransactionSQLRepository[] repositories = new TransactionSQLRepository[size];
        ArrayBlockingQueue<TransactionSQLRepository> idle = new ArrayBlockingQueue<>(size);
        try {
            for (int index = 0; index < size; index++) {
                repositories[index] = TransactionSQLRepository.open();
                idle.add(repositories[index]);
            }
        } catch (SQLException | RuntimeException error) {
            try {
                closeAll(repositories);
            } catch (SQLException closeError) {
                error.addSuppressed(closeError);
            }
            if (error instanceof SQLException sql) {
                throw sql;
            }
            throw (RuntimeException) error;
        }
        return new InsertConnections(repositories, idle);
    }

    private InsertConnections(
            TransactionSQLRepository[] repositories,
            ArrayBlockingQueue<TransactionSQLRepository> idle) {
        this.repositories = repositories;
        this.idle = idle;
    }

    void saveAll(List<Transaction> batch) {
        TransactionSQLRepository repository;
        try {
            repository = idle.take();
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("ingestion was interrupted", error);
        }
        try {
            repository.saveAll(batch);
        } finally {
            idle.offer(repository);
        }
    }

    @Override
    public void close() throws SQLException {
        closeAll(repositories);
    }

    private static void closeAll(TransactionSQLRepository[] repositories) throws SQLException {
        SQLException failure = null;
        for (TransactionSQLRepository repository : repositories) {
            if (repository == null) {
                continue;
            }
            try {
                repository.close();
            } catch (SQLException error) {
                if (failure == null) {
                    failure = error;
                } else {
                    failure.addSuppressed(error);
                }
            }
        }
        if (failure != null) {
            throw failure;
        }
    }
}
