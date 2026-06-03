package br.com.zenon.repository;

import br.com.zenon.config.ConnectionFactory;
import br.com.zenon.domain.Transaction;
import br.com.zenon.domain.TransactionCustumer;
import br.com.zenon.domain.TransactionType;
import br.com.zenon.domain.TransactionRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class TransactionSQLRepository implements TransactionRepository {

    public static final int JDBC_BATCH_SIZE = 1000;
    Logger logger = Logger.getLogger(TransactionSQLRepository.class.getName());

    @Override
    public void save(Transaction transaction) {
        String sql = """
                insert into transactions
                (step, `type` , amount, name_origin, old_balance_origin, new_balance_origin, name_recipient, old_balance_recipient, new_balance_recipient, is_fraud, is_flagged_fraud)
                values
                (?,?,?,?,?,?,?,?,?,?, ?);
                """;

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, transaction.step());
            ps.setString(2, transaction.type().name());
            ps.setBigDecimal(3, transaction.amount());

            ps.setString(4, transaction.origin().name());
            ps.setBigDecimal(5, transaction.origin().oldBalance());
            ps.setBigDecimal(6, transaction.origin().newBalance());

            ps.setString(7, transaction.recipient().name());
            ps.setBigDecimal(8, transaction.recipient().oldBalance());
            ps.setBigDecimal(9, transaction.recipient().newBalance());

            ps.setBoolean(10, transaction.isFraud());
            ps.setBoolean(11, transaction.isFlaggedFraud());

            ps.execute();


        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar nova transação " + e);
        }

    }

    @Override
    public Optional<Transaction> findByOriginName(String originName) {


        String sql = """
                SELECT id, step, `type`, amount, name_origin, old_balance_origin,
                       new_balance_origin, name_recipient, old_balance_recipient, 
                       new_balance_recipient, is_fraud, is_flagged_fraud
                FROM zenon_frauds.transactions
                WHERE name_origin = ?
                ORDER BY step
                LIMIT 1""";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);) {

            ps.setString(1, originName);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Transaction transaction = mapResultSetToTransaction(rs);
                    return Optional.of(transaction);
                } else {
                    IO.println("Transação não encontrada para: " + originName);
                    return Optional.empty();
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar transação da origem : " + originName, e);
        }

    }

    private Transaction mapResultSetToTransaction(ResultSet rs) {
        try {
            int step = rs.getInt("step");
            TransactionType type = TransactionType.valueOf(rs.getString("type"));
            BigDecimal amount = rs.getBigDecimal("amount");

            String originName = rs.getString("name_origin");
            BigDecimal originOldBalance = rs.getBigDecimal("old_balance_origin");
            BigDecimal originNewBalance = rs.getBigDecimal("new_balance_origin");

            TransactionCustumer origin = new TransactionCustumer(originName, originOldBalance, originNewBalance);

            String recipientName = rs.getString("name_recipient");
            BigDecimal recipientOldBalance = rs.getBigDecimal("old_balance_recipient");
            BigDecimal recipientNewBalance = rs.getBigDecimal("new_balance_recipient");
            TransactionCustumer recipient = new TransactionCustumer(recipientName, recipientOldBalance, recipientNewBalance);

            boolean isFraud = rs.getBoolean("is_fraud");
            boolean isFlaggedFraud = rs.getBoolean("is_flagged_fraud");

            return new Transaction(step, type, amount, origin, recipient, isFraud, isFlaggedFraud);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveAll(List<Transaction> transactions) {
        String sql = """
                insert into transactions
                (step, `type` , amount, name_origin, old_balance_origin, new_balance_origin, name_recipient, old_balance_recipient, new_balance_recipient, is_fraud, is_flagged_fraud)
                values
                (?,?,?,?,?,?,?,?,?,?, ?);
                """;
        try (Connection connection = ConnectionFactory.getConnection();) {
            connection.setAutoCommit(false);

            int count = 0;
            try (PreparedStatement ps = connection.prepareStatement(sql)) {

                for (Transaction transaction : transactions) {


                    ps.setInt(1, transaction.step());
                    ps.setString(2, transaction.type().name());
                    ps.setBigDecimal(3, transaction.amount());

                    ps.setString(4, transaction.origin().name());
                    ps.setBigDecimal(5, transaction.origin().oldBalance());
                    ps.setBigDecimal(6, transaction.origin().newBalance());

                    ps.setString(7, transaction.recipient().name());
                    ps.setBigDecimal(8, transaction.recipient().oldBalance());
                    ps.setBigDecimal(9, transaction.recipient().newBalance());

                    ps.setBoolean(10, transaction.isFraud());
                    ps.setBoolean(11, transaction.isFlaggedFraud());

                    ps.addBatch();
                    count++;

                    if (count % JDBC_BATCH_SIZE == 0) {
                        IO.println("Executando batch JDBC...");

                        ps.executeBatch();
                        connection.commit();
                    }
                }
                IO.println("Executando batch final JDBC...");

                ps.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);

            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Erro ao executar rollback" + ex);
                }
                throw new RuntimeException("Erro ao salvar nova transação " + e);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar no banco de dados", e);
        }

    }
}
