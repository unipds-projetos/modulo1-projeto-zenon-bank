package br.com.zenon.fraud;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

final class MysqlAvailability {

    private static final boolean UP = probe();

    private MysqlAvailability() {
    }

    static boolean isUp() {
        return UP;
    }

    private static boolean probe() {
        DriverManager.setLoginTimeout(2);
        try (Connection connection = DriverManager.getConnection(
                JdbcSettings.url(), JdbcSettings.user(), JdbcSettings.password())) {
            return connection.isValid(2);
        } catch (SQLException error) {
            return false;
        }
    }
}
