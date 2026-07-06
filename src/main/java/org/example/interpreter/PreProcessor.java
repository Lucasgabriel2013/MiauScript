package org.example.interpreter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PreProcessor {
    public String preprocess(String code, Path parent) {
        String[] lines = code.split("\n");

        for (String line : lines) {
            if (!line.isEmpty() && !line.startsWith("#") && !line.startsWith(":/")) break;

            if (line.matches("#copycat .*")) {
                if (line.matches("#copycat \".*\"")) {
                    String path = line.substring(10, line.length() - 1);

                    try {
                        String s = Files.readString(parent.resolve(path));
                        code = code.replace(line, preprocess(s, parent));
                    } catch (IOException _) {
                        throw new MiauScriptException("Arquivo do Copycat não encontrado");
                    }
                }
            }
        }
        return code;
    }
}
