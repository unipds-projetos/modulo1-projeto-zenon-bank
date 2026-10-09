package br.com.zenon.fraud.security;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Optional;

public final class MethodGuard {

    private MethodGuard() {
    }

    public static <T> T notNull(T value, String name) {
        return InputGuard.requireNonNull(value, name);
    }

    public static String customerName(String name) {
        return InputGuard.requireCustomerName(name);
    }

    public static Optional<String> customerQuery(String name) {
        return InputGuard.optionalCustomerName(name);
    }

    public static BigDecimal money(BigDecimal amount, String name) {
        return InputGuard.requireMoney(amount, name);
    }

    public static int step(int step) {
        return InputGuard.requireStep(step);
    }

    public static int limit(int limit) {
        return InputGuard.requireLimit(limit);
    }

    public static boolean fraudFlag(String value) {
        return InputGuard.requireFlag(value);
    }

    public static void requireNoArguments(String[] args) {
        InputGuard.requireNoArguments(args);
    }

    public static Path dataFile(String fileName) throws IOException {
        return PathGuard.csvInsideData(fileName);
    }

    public static String rejectedRow(String line) {
        return DisclosureGuard.rejectedRow(line);
    }
}
