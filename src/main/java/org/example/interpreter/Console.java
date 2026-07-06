package org.example.interpreter;

public interface Console {
    void print(Object what);
    void println(Object what);
    void clear();
    String input();
}
