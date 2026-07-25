package org.example;

import org.example.interpreter.*;
import org.example.screen.ColorPanel;
import org.example.screen.Frame;
import org.example.screen.Terminal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Quantidade de argumentos invalido");
            return;
        }

        Path path = Path.of(args[0]);

        Terminal terminal = new Terminal();
        ColorPanel colorPanel = new ColorPanel();

        new Frame(colorPanel, terminal);

        String code;

        try {
            var file = Files.readString(path);
            code = new PreProcessor().preprocess(file, path.toAbsolutePath().getParent());
        } catch (MiauScriptException e) {
            terminal.println(e.getMessage());
            return;
        }

        Compiler compiler = new Compiler();
        Program program = compiler.compile(List.of(code.split("\n")));

        try {
            new CodeInterpreter(new Context(terminal, colorPanel, program, true), "main");
        } catch (MiauScriptException e) {
            terminal.println(e.getMessage() + (e.getLine().isEmpty() ? "" :" \"" + e.getLine() + "\""));
        }
    }
}
