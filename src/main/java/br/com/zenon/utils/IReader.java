package br.com.zenon.utils;

import java.io.IOException;
import java.util.List;

public interface IReader<T> {
    List<T> read(IMapper<T> mapper, String path, int limit) throws IOException;
}
