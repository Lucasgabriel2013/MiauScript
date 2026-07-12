package org.example.interpreter;

import org.example.screen.Key;

import java.util.Scanner;

public class SystemConsole implements Console {
    private final Scanner s = new Scanner(System.in);

    @Override
    public void print(Object what) {
        System.out.print(what);
    }

    @Override
    public void println(Object what) {
        System.out.println(what);
    }

    @Override
    public void clear() {
        System.out.println("\n".repeat(100));
    }

    @Override
    public boolean isPressed(Key key) {
        return false;
    }

    @Override
    public String input() {
        return s.nextLine();
    }
}
