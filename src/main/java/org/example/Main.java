package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    static void main(String[] args) throws IOException {
        var code = Files.readString(Path.of(args[0]));

        String[] lines = code.split("\n");

        new CodeInterpreter(lines);
    }

}
