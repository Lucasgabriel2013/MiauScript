package org.example.interpreter;

import org.example.screen.Key;

public interface Console {
    void print(Object what);
    void println(Object what);
    void clear();
    boolean isPressed(Key key);
    String input();
}
