package br.com.zenon.fraud;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private ConnectionFactory(){}

    public static Connection getConnection(){
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/zenon_frauds?rewriteBatchedStatements=true", "root", "senha123");
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting with database: ", e);
        }
    }
}
