package br.com.zenon.fraud.security;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

final class InputGuard {

    private static final Pattern CUSTOMER_NAME = Pattern.compile("^[CM][0-9]{1,20}$");
    private static final BigDecimal MAX_MONEY = new BigDecimal("1000000000000.00");
    private static final int MIN_STEP = 1;
    private static final int MAX_STEP = 10_000;
    private static final int MAX_LIMIT = 1_000;

    private InputGuard() {
    }

    static <T> T requireNonNull(T value, String name) {
        return Objects.requireNonNull(value, name);
    }

    static String requireCustomerName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name should not be empty");
        }
        if (!CUSTOMER_NAME.matcher(name).matches()) {
            throw new IllegalArgumentException("name has an invalid format");
        }
        return name;
    }

    static Optional<String> optionalCustomerName(String name) {
        if (name == null || !CUSTOMER_NAME.matcher(name).matches()) {
            return Optional.empty();
        }
        return Optional.of(name);
    }

    static BigDecimal requireMoney(BigDecimal amount, String name) {
        Objects.requireNonNull(amount, name);
        if (amount.signum() < 0) {
            throw new IllegalArgumentException(name + " should be positive: " + amount);
        }
        BigDecimal normalized = amount.scale() < 0 ? amount.setScale(0) : amount;
        if (normalized.scale() > 2 || normalized.compareTo(MAX_MONEY) > 0) {
            throw new IllegalArgumentException(name + " is outside the allowed range");
        }
        return normalized;
    }

    static int requireStep(int step) {
        if (step < MIN_STEP) {
            throw new IllegalArgumentException("step should be positive: " + step);
        }
        if (step > MAX_STEP) {
            throw new IllegalArgumentException("step is outside the allowed range");
        }
        return step;
    }

    static int requireLimit(int limit) {
        if (limit < 0 || limit > MAX_LIMIT) {
            throw new IllegalArgumentException("limit is outside the allowed range");
        }
        return limit;
    }

    static boolean requireFlag(String value) {
        return switch (value) {
            case "0" -> false;
            case "1" -> true;
            default -> throw new IllegalArgumentException("flag should be 0 or 1");
        };
    }

    static void requireNoArguments(String[] args) {
        if (args == null || args.length > 0) {
            throw new IllegalArgumentException("this entry point does not accept arguments");
        }
    }
}
