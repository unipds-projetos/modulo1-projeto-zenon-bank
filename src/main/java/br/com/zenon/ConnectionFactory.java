package br.com.zenon;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private ConnectionFactory(){

    }
    public static  Connection getConnection(){
        try {

            return DriverManager.getConnection("jdbc:mysql://localhost:3308/zenon_frauds?rewriteBatchedStatements=true", "root", "senha123");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar com o JDBC",e);
        }
    }
}
