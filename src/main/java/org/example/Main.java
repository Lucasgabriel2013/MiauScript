package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    static void main(String[] args) throws IOException {
        Path path = Path.of(args[0]);

        var code = Files.readString(path);
        String[] lines;

        try {
            lines = new PreProcessor().preprocess(code, path.toAbsolutePath().getParent()).split("\n");
        } catch (MiauScriptException e) {
            System.err.println(e.getMessage());
            return;
        }

        try {
            new CodeInterpreter(lines);
        } catch (MiauScriptException e) {
            System.err.println(e.getMessage() + (e.getLine().isEmpty() ? "" :" \"" + e.getLine() + "\""));
        }
    }
}
