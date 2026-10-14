package br.com.zenon.fraud;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import br.com.zenon.fraud.security.MethodGuard;

public final class TransactionSQLRepository implements TransactionRepository, AutoCloseable {

    private static final int BATCH_SIZE = 1_000;
    private static final String INSERT = """
            INSERT INTO TRANSACTIONS (
                step, type, amount,
                origin_name, origin_old_balance, origin_new_balance,
                recipient_name, recipient_old_balance, recipient_new_balance,
                is_fraud, is_flagged_fraud)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
    private static final String SELECT_BY_ORIGIN = """
            SELECT step, type, amount,
                   origin_name, origin_old_balance, origin_new_balance,
                   recipient_name, recipient_old_balance, recipient_new_balance,
                   is_fraud, is_flagged_fraud
            FROM TRANSACTIONS
            WHERE origin_name = ?
            ORDER BY id
            LIMIT 1
            """;

    private final Connection connection;

    public static TransactionSQLRepository open() throws SQLException {
        Connection connection = DriverManager.getConnection(JdbcSettings.url(), JdbcSettings.user(), JdbcSettings.password());
        return new TransactionSQLRepository(connection);
    }

    TransactionSQLRepository(Connection connection) {
        this.connection = MethodGuard.notNull(connection, "connection");
    }

    @Override
    public Optional<Transaction> findByOriginName(String originName) {
        Optional<String> name = MethodGuard.customerQuery(originName);
        if (name.isEmpty()) {
            return Optional.empty();
        }
        try (PreparedStatement statement = connection.prepareStatement(SELECT_BY_ORIGIN)) {
            statement.setString(1, name.get());
            try (ResultSet rows = statement.executeQuery()) {
                if (!rows.next()) {
                    return Optional.empty();
                }
                return Optional.of(read(rows));
            }
        } catch (SQLException error) {
            throw new IllegalStateException("database operation failed", error);
        }
    }

    @Override
    public void save(Transaction transaction) {
        saveAll(List.of(MethodGuard.notNull(transaction, "transaction")));
    }

    public void saveAll(List<Transaction> transactions) {
        List<Transaction> rows = List.copyOf(MethodGuard.notNull(transactions, "transactions"));
        if (rows.isEmpty()) {
            return;
        }
        try {
            boolean previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(INSERT)) {
                int pending = 0;
                for (Transaction transaction : rows) {
                    bind(statement, transaction);
                    statement.addBatch();
                    pending++;
                    if (pending == BATCH_SIZE) {
                        statement.executeBatch();
                        pending = 0;
                    }
                }
                if (pending > 0) {
                    statement.executeBatch();
                }
                connection.commit();
            } catch (SQLException error) {
                connection.rollback();
                throw error;
            } finally {
                connection.setAutoCommit(previousAutoCommit);
            }
        } catch (SQLException error) {
            throw new IllegalStateException("database operation failed", error);
        }
    }

    public void deleteAll() {
        try (Statement statement = connection.createStatement()) {
            statement.execute("TRUNCATE TABLE TRANSACTIONS");
        } catch (SQLException error) {
            throw new IllegalStateException("database operation failed", error);
        }
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }

    private static void bind(PreparedStatement statement, Transaction transaction) throws SQLException {
        statement.setInt(1, transaction.step());
        statement.setString(2, transaction.type().name());
        statement.setBigDecimal(3, transaction.amount());
        statement.setString(4, transaction.origin().name());
        statement.setBigDecimal(5, transaction.origin().oldBalance());
        statement.setBigDecimal(6, transaction.origin().newBalance());
        statement.setString(7, transaction.recipient().name());
        statement.setBigDecimal(8, transaction.recipient().oldBalance());
        statement.setBigDecimal(9, transaction.recipient().newBalance());
        statement.setBoolean(10, transaction.isFraud());
        statement.setBoolean(11, transaction.isFlaggedFraud());
    }

    private static Transaction read(ResultSet rows) throws SQLException {
        return new Transaction(
                rows.getInt("step"),
                TransactionType.valueOf(rows.getString("type")),
                rows.getBigDecimal("amount"),
                new TransactionCustomer(
                        rows.getString("origin_name"),
                        rows.getBigDecimal("origin_old_balance"),
                        rows.getBigDecimal("origin_new_balance")),
                new TransactionCustomer(
                        rows.getString("recipient_name"),
                        rows.getBigDecimal("recipient_old_balance"),
                        rows.getBigDecimal("recipient_new_balance")),
                rows.getBoolean("is_fraud"),
                rows.getBoolean("is_flagged_fraud"));
    }
}
