package br.com.zenon.fraud;

import java.util.function.Function;

final class JdbcSettings {

    static final String URL = "jdbc:mysql://127.0.0.1:3306/zenon"
            + "?rewriteBatchedStatements=true"
            + "&sslMode=DISABLED"
            + "&allowPublicKeyRetrieval=true"
            + "&connectTimeout=5000";
    static final String USER = "zenon";
    static final String PASSWORD = "zenon";

    private JdbcSettings() {
    }

    static String url() {
        return configured("ZENON_JDBC_URL", URL, JdbcSettings::requireMysqlUrl);
    }

    static String user() {
        return configured("ZENON_JDBC_USER", USER, value -> requireCredential(value, "user"));
    }

    static String password() {
        return configured("ZENON_JDBC_PASSWORD", PASSWORD, value -> requireCredential(value, "password"));
    }

    static String requireMysqlUrl(String url) {
        if (url == null
                || !url.startsWith("jdbc:mysql://")
                || url.indexOf('@') >= 0
                || url.indexOf(' ') >= 0
                || url.indexOf('\r') >= 0
                || url.indexOf('\n') >= 0) {
            throw new IllegalArgumentException("jdbc url is invalid");
        }
        return url;
    }

    private static String requireCredential(String value, String name) {
        if (value == null || value.isBlank() || value.indexOf('\r') >= 0 || value.indexOf('\n') >= 0) {
            throw new IllegalArgumentException(name + " is invalid");
        }
        return value;
    }

    private static String configured(String variable, String defaultValue, Function<String, String> validator) {
        String configured = System.getenv(variable);
        if (configured == null || configured.isBlank()) {
            return defaultValue;
        }
        return validator.apply(configured);
    }
}
