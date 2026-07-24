package org.example.interpreter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PreProcessor {
    private final List<String> paths = new ArrayList<>();

    public String preprocess(String code, Path parent) {
        String[] lines = code.split("\n");

        for (String line : lines) {
            if (!line.isEmpty() && !line.startsWith("#")) break;

            if (line.matches("#copycat .*")) {
                if (line.matches("#copycat \".*\"")) {
                    String path = line.substring(10, line.length() - 1);

                    try {
                        String s = Files.readString(parent.resolve(path));
                        String preprocessed = preprocess(s, parent);

                        if (paths.contains(path)) {
                            code = code.replaceFirst(line, "");
                            return code;
                        }

                        code = code.replaceFirst(line, preprocessed);
                        paths.add(path);
                    } catch (IOException _) {
                        throw new MiauScriptException("Arquivo do Copycat não encontrado");
                    }
                }
            }
        }
        return code;
    }
}
