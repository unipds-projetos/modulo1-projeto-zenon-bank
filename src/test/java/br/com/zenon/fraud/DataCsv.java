package br.com.zenon.fraud;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

final class DataCsv implements AutoCloseable {

    private final Path path;

    private DataCsv(Path path) {
        this.path = path;
    }

    static DataCsv write(String content) throws IOException {
        DataCsv csv = allocate();
        Files.writeString(csv.path, content);
        return csv;
    }

    static DataCsv allocate() throws IOException {
        Path data = Path.of("data");
        Files.createDirectories(data);
        return new DataCsv(data.resolve("zz-test-" + UUID.randomUUID() + ".csv"));
    }

    Path file() {
        return path;
    }

    String relativePath() {
        return "data/" + path.getFileName();
    }

    @Override
    public void close() throws IOException {
        Files.deleteIfExists(path);
    }
}
