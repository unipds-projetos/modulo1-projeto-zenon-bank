package br.com.zenon;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class TransactionSQLRepository implements TransactionRepository {

    public static final int JDBC_BATCH_SIZE = 1_000;

    @Override
    public Optional<Transaction> findByOriginName(String name) {

        String sql = """
                SELECT id, step, `type`, amount, name_origin, old_balance_origin,
                    new_balance_origin, name_recipient, old_balance_recipient, new_balance_recipient,
                    is_fraud, is_flagged_fraud
                FROM zenon_frauds.transactions
                WHERE name_origin = ?
                ORDER BY step
                LIMIT 1
                """;


        try(Connection connection = ConnectionFactory.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, name);

            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){

                    System.out.println(rs.getString("name_origin"));

                    Transaction transaction = mapResultSetToTranscation(rs);
                    return Optional.of(transaction);


                }else {
                    System.out.println("transaction nao encontrada");
                    return Optional.empty();
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar origin por nome:"+name,e);
        }

    }

    public void saveALL(List<Transaction> transactions){



        String sql = """
               INSERT INTO zenon_frauds.transactions (step, `type`, amount, name_origin, old_balance_origin,
                    new_balance_origin, name_recipient, old_balance_recipient, new_balance_recipient,
                    is_fraud, is_flagged_fraud) values (?,?,?,?,?,?,?,?,?,?,?)
               """;

        try (Connection connection = ConnectionFactory.getConnection()){
            connection.setAutoCommit(false);

            int count = 0;
                try(PreparedStatement ps = connection.prepareStatement(sql)) {
                    for(Transaction transaction: transactions){

                        ps.setInt(1,transaction.step());
                        ps.setString(2, transaction.type().name());
                        ps.setBigDecimal(3, transaction.amount());

                        ps.setString(4, transaction.origin().name());
                        ps.setBigDecimal(5,transaction.origin().oldBalance());
                        ps.setBigDecimal(6,transaction.origin().newBalance());

                        ps.setString(7,transaction.recipient().name());
                        ps.setBigDecimal(8, transaction.recipient().oldBalance());
                        ps.setBigDecimal(9,transaction.recipient().newBalance());

                        ps.setBoolean(10, transaction.isFraud());
                        ps.setBoolean(11, transaction.isFlaggedFraud());

//                        System.out.println("salvando transaction: "+transaction.step()+" - "+transaction.amount());

    //                    ps.executeUpdate();
                        ps.addBatch();
                        count++;

                        if(count % JDBC_BATCH_SIZE == 0){
                            System.out.println("Executando batch...");
                            ps.executeBatch();
                            connection.commit();
                        }
                    }

                    System.out.println("executando batch final...");

                    ps.executeBatch();
                    connection.commit();
                    connection.setAutoCommit(true);

                } catch (SQLException e) {

                    try{
                        connection.rollback();
                    } catch (SQLException ex){
                        throw new RuntimeException("erro no rollback", ex);
                    }

                    throw new RuntimeException("Erro ao salvar nova transaction: "+e);

            }

        } catch (SQLException e) {

            throw  new RuntimeException("Erro an conexao com o BD...", e);

        }




    };
    @Override
    public void save(Transaction transaction) {

        String sql = """
               INSERT INTO zenon_frauds.transactions (step, `type`, amount, name_origin, old_balance_origin,
                    new_balance_origin, name_recipient, old_balance_recipient, new_balance_recipient,
                    is_fraud, is_flagged_fraud) values (?,?,?,?,?,?,?,?,?,?,?)
               """;


        try(Connection connection = ConnectionFactory.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1,transaction.step());
            ps.setString(2, transaction.type().name());
            ps.setBigDecimal(3, transaction.amount());

            ps.setString(4, transaction.origin().name());
            ps.setBigDecimal(5,transaction.origin().oldBalance());
            ps.setBigDecimal(6,transaction.origin().newBalance());

            ps.setString(7,transaction.recipient().name());
            ps.setBigDecimal(8, transaction.recipient().oldBalance());
            ps.setBigDecimal(9,transaction.recipient().newBalance());

            ps.setBoolean(10, transaction.isFraud());
            ps.setBoolean(11, transaction.isFlaggedFraud());

            ps.executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar nova transaction: "+e);
        }

    }

    private Transaction mapResultSetToTranscation(ResultSet rs) {
        try {
            int step = rs.getInt("step");
            TransactionType type = TransactionType.valueOf(rs.getString("type"));
            BigDecimal amount = rs.getBigDecimal("amount");


            String originName = rs.getString("name_origin");
            BigDecimal originOldBalance = rs.getBigDecimal("old_balance_origin");
            BigDecimal originNewBalance = rs.getBigDecimal("new_balance_origin");
            TransactionCostumer origin = new TransactionCostumer(originName, originOldBalance, originNewBalance);

            String recipientName = rs.getString("name_recipient");
            BigDecimal recipientOldBalance = rs.getBigDecimal("old_balance_recipient");
            BigDecimal recipientNewBalance = rs.getBigDecimal("new_balance_recipient");
            TransactionCostumer recipient = new TransactionCostumer(recipientName, recipientOldBalance, recipientNewBalance);

            boolean isFraud  = rs.getBoolean("is_fraud");
            boolean isFlaggedFraud  = rs.getBoolean("is_flagged_fraud");

            return new Transaction(step, type, amount, origin, recipient, isFraud, isFlaggedFraud);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
