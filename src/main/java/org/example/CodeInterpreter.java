package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CodeInterpreter {
    Map<String, Object> vars = new HashMap<>();
    Map<String, Integer> labels = new HashMap<>();

    Scanner scanner = new Scanner(System.in);

    int currentLine;

    public CodeInterpreter(String[] lines) {
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            line = line.trim();

            if (line.matches("[A-Za-z_]+:")) {
                labels.put(line.substring(0, line.length() - 1), i);
            }
        }
        for (currentLine = 0; currentLine < lines.length; currentLine++) {
            String line = lines[currentLine];

            executeLine(line);
        }
    }

    private void executeLine(String line) {
        line = line.trim();

        if (line.isEmpty() || line.startsWith(":/") || line.matches("[A-Za-z_]+:")) return;

        if (line.matches("meow \\(.*\\)")) {
            if (line.matches("meow \\(\".*\"\\)")) {
                System.out.println(line.substring(7, line.length() - 2));
            } else {
                if (vars.containsKey(line.substring(6, line.length() - 1))) {
                    System.out.println(vars.get(line.substring(6, line.length() - 1)));
                } else {
                    System.out.println(ExpressionInterpreter.interpret(line.substring(6, line.length() - 1), this));
                }
            }
        } else if (line.matches("eat [A-Za-z_]+ = .+")) {
            if (line.matches("eat [A-Za-z_]+ = \".+\"")) {
                int equalIndex = line.indexOf("=");

                vars.put(line.substring(4, equalIndex - 1), line.substring(equalIndex + 3, line.length() - 1));
            } else {
                int equalIndex = line.indexOf("=");

                vars.put(line.substring(4, equalIndex - 1),
                        ExpressionInterpreter.interpret(line.substring(equalIndex + 2), this));
            }
        } else if (line.matches("input [A-Za-z_]+")) {
            vars.put(line.substring(6), scanner.nextLine());
        } else if (line.matches("call [A-Za-z_]+")) {
            if (labels.containsKey(line.substring(5))) {
                currentLine = labels.get(line.substring(5));
            }
        } else if (line.matches("if \\(.*\\) then: .*")) {
            if (ExpressionInterpreter.interpret(line.substring(4, line.indexOf(")")), this) == 1f) {
                executeLine(line.substring(line.indexOf(")") + 7));
            }
        } else if (line.matches("return")) {

        } else {
            throw new RuntimeException("Arruma o código, erro na linha " + currentLine + " (\"" + line + "\")");
        }
    }
}
