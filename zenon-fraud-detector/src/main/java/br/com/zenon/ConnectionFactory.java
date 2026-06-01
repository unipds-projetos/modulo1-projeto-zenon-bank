package br.com.zenon;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private ConnectionFactory(){}

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/zenon_frauds", "root", "senha123");
        }catch (SQLException ex){
            throw new RuntimeException("Erro ao conectar no banco de dados", ex);
        }
    }
}
