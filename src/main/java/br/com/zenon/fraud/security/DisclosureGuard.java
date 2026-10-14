package br.com.zenon.fraud.security;

final class DisclosureGuard {

    private DisclosureGuard() {
    }

    static String rejectedRow(String line) {
        int length = line == null ? 0 : line.length();
        return "linha rejeitada (" + length + " caracteres)";
    }
}
