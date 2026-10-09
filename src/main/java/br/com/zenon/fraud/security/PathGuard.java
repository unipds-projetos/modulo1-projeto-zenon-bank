package br.com.zenon.fraud.security;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

final class PathGuard {

    private PathGuard() {
    }

    static Path csvInsideData(String fileName) throws IOException {
        if (fileName == null || fileName.isBlank() || fileName.indexOf('\0') >= 0) {
            throw new IllegalArgumentException("fileName is invalid");
        }

        Path dataRoot = Path.of("data").toAbsolutePath().normalize();
        if (!Files.isDirectory(dataRoot)) {
            throw new IOException("data directory is not available");
        }

        Path candidate = Path.of(fileName);
        if (candidate.isAbsolute()) {
            throw new IllegalArgumentException("fileName must stay inside data/");
        }

        Path resolved = Path.of("").toAbsolutePath().resolve(candidate).normalize();
        if (!resolved.startsWith(dataRoot) || resolved.equals(dataRoot)) {
            throw new IllegalArgumentException("fileName must stay inside data/");
        }

        String filename = resolved.getFileName().toString();
        if (!filename.endsWith(".csv")) {
            throw new IllegalArgumentException("fileName must end with .csv");
        }
        if (!Files.isRegularFile(resolved, LinkOption.NOFOLLOW_LINKS)) {
            if (Files.isSymbolicLink(resolved)) {
                throw new IllegalArgumentException("symbolic links are not allowed");
            }
            throw new NoSuchFileException(filename);
        }

        Path realRoot = dataRoot.toRealPath();
        Path realParent = resolved.getParent().toRealPath();
        if (!realParent.startsWith(realRoot)) {
            throw new IllegalArgumentException("fileName must stay inside data/");
        }
        return resolved;
    }
}
