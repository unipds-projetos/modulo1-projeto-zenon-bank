package br.com.zenon.fraud;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

final class StderrCapture implements AutoCloseable {

    private final PrintStream original;
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    StderrCapture() {
        original = System.err;
        System.setErr(new PrintStream(buffer, true, StandardCharsets.UTF_8));
    }

    String text() {
        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Override
    public void close() {
        System.setErr(original);
    }
}
