package br.com.zenon.frauddetector;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AplicacaoTest {

    @Test
    void printsTheBankBannerAndTheRunningJavaVersion() {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        try {
            Aplicacao.main(new String[0]);
        } finally {
            System.setOut(original);
        }

        String output = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Zenón Bank — fraud detector"));
        assertTrue(output.contains("Java " + Runtime.version()));
    }

    @Test
    void rejectsProgramArgumentsBeforePrinting() {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        try {
            IllegalArgumentException error = assertThrows(
                    IllegalArgumentException.class, () -> Aplicacao.main(new String[] {"arquivo.csv"}));
            assertEquals("this entry point does not accept arguments", error.getMessage());
        } finally {
            System.setOut(original);
        }

        assertEquals("", buffer.toString(StandardCharsets.UTF_8));
    }
}
