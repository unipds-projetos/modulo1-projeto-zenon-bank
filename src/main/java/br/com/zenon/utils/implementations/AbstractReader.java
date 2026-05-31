package br.com.zenon.utils.implementations;

import br.com.zenon.utils.IMapper;
import br.com.zenon.utils.IReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractReader<T> implements IReader<T> {
    @Override
    public List<T> read(IMapper<T> mapper, String path, int limit) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Path.of(path), StandardCharsets.UTF_8)) {
            String headerLine = reader.readLine();
            if (headerLine == null) return List.of();
            mapper.initializeHeaders(headerLine.split(","));
            List<T> list = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                list.add(mapper.parse(line.split(",")));
                if (list.size() >= limit) break;  // stop early
            }
            return list;
        }
    }

    protected abstract List<T> parse(String[] lines, IMapper<T> mapper);
}
