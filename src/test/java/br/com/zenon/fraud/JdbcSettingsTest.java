package br.com.zenon.fraud;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JdbcSettingsTest {

    @Test
    void defaultsToTheLocalMysqlAndEnablesBatchRewriting() {
        assertTrue(JdbcSettings.URL.startsWith("jdbc:mysql://127.0.0.1:3306/zenon"));
        assertTrue(JdbcSettings.URL.contains("rewriteBatchedStatements=true"));
        assertEquals("zenon", JdbcSettings.USER);
    }

    @Test
    void acceptsOnlyAMysqlUrlWithoutCredentialsInTheAddress() {
        assertEquals(
                "jdbc:mysql://127.0.0.1:3307/zenon",
                JdbcSettings.requireMysqlUrl("jdbc:mysql://127.0.0.1:3307/zenon"));

        assertEquals(
                "jdbc url is invalid",
                assertThrows(IllegalArgumentException.class, () -> JdbcSettings.requireMysqlUrl(null)).getMessage());
        assertEquals(
                "jdbc url is invalid",
                assertThrows(IllegalArgumentException.class,
                        () -> JdbcSettings.requireMysqlUrl("jdbc:mysql://zenon:zenon@127.0.0.1:3306/zenon")).getMessage());
        assertEquals(
                "jdbc url is invalid",
                assertThrows(IllegalArgumentException.class,
                        () -> JdbcSettings.requireMysqlUrl("jdbc:h2:mem:zenon")).getMessage());
        assertEquals(
                "jdbc url is invalid",
                assertThrows(IllegalArgumentException.class,
                        () -> JdbcSettings.requireMysqlUrl("jdbc:mysql://127.0.0.1/zenon\n")).getMessage());
    }
}
