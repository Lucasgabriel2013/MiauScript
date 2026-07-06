package org.example.interpreter;

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
        // ADAPTAÇÃO TÉCNICA! (GAMBIARRA)
        System.out.println("\n".repeat(100));
    }

    @Override
    public String input() {
        return s.nextLine();
    }
}
