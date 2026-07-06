package org.example;

import org.example.interpreter.CodeInterpreter;
import org.example.interpreter.MiauScriptException;
import org.example.interpreter.PreProcessor;
import org.example.screen.ColorPanel;
import org.example.screen.Frame;
import org.example.screen.Terminal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Quantidade de argumentos invalido");
            return;
        }

        Path path = Path.of(args[0]);

        var file = Files.readString(path);
        String code;

        Terminal terminal = new Terminal();
        new Frame(new ColorPanel(), terminal);

        try {
            code = new PreProcessor().preprocess(file, path.toAbsolutePath().getParent());
        } catch (MiauScriptException e) {
            System.err.println(e.getMessage());
            return;
        }

        try {
            new CodeInterpreter(code.split("\n"), terminal);
        } catch (MiauScriptException e) {
            System.err.println(e.getMessage() + (e.getLine().isEmpty() ? "" :" \"" + e.getLine() + "\""));
        }
    }
}
