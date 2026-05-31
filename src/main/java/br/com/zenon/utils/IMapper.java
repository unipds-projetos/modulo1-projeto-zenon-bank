package br.com.zenon.utils;

public interface IMapper<T> {
    T parse(String[] values);
    void initializeHeaders(String[] headers);
}
