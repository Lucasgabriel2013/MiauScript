package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

public class CodeInterpreter {
    Map<String, Object> vars = new HashMap<>();
    Map<String, Integer> labels = new HashMap<>();

    Stack<Integer> calls = new Stack<>();

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

        if (!labels.containsKey("main")) {
            throw new RuntimeException("Label main não encontrada");
        }

        for (currentLine = labels.get("main"); currentLine < lines.length; currentLine++) {
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
        } else if (line.matches("purr \\(.*\\)")) {
            if (line.matches("purr \\(\".*\"\\)")) {
                System.out.print(line.substring(7, line.length() - 2));
            } else {
                if (vars.containsKey(line.substring(6, line.length() - 1))) {
                    System.out.print(vars.get(line.substring(6, line.length() - 1)));
                } else {
                    System.out.print(ExpressionInterpreter.interpret(line.substring(6, line.length() - 1), this));
                }
            }
        } else if (line.matches("eat .+ = .+")) {
            if (line.matches("eat [A-Za-z_]+ = \".+\"")) {
                int equalIndex = line.indexOf("=");

                vars.put(line.substring(4, equalIndex - 1), line.substring(equalIndex + 3, line.length() - 1));
            } else if (line.matches("eat [A-Za-z_]+\\[.*] = .+")) {
                if (line.matches("eat [A-Za-z_]+\\[.*] = \".+\"")) {
                    @SuppressWarnings("unchecked")
                    HashMap<Integer, Object> map = (HashMap<Integer, Object>) vars.get(line.substring(4, line.indexOf("[")));
                    map.put((int) ExpressionInterpreter.interpret(line.substring(line.indexOf("[") + 1, line.indexOf("]")), this), line.substring(line.indexOf("\"") + 1, line.length() - 1));
                } else if (line.matches("eat [A-Za-z_]+\\[.*] = .+")) {
                    @SuppressWarnings("unchecked")
                    HashMap<Integer, Object> map = (HashMap<Integer, Object>) vars.get(line.substring(4, line.indexOf("[")));
                    map.put((int) ExpressionInterpreter.interpret(line.substring(line.indexOf("[") + 1, line.indexOf("]")), this), ExpressionInterpreter.interpret(line.substring(line.indexOf("=") + 1), this));
                }
            } else {
                int equalIndex = line.indexOf("=");

                vars.put(line.substring(4, equalIndex - 1), ExpressionInterpreter.interpret(line.substring(equalIndex + 2), this));
            }
        } else if (line.matches("input [A-Za-z_]+")) {
            vars.put(line.substring(6), scanner.nextLine());
        } else if (line.matches("random [A-Za-z_]+ = [0-9]+")) {
            int lastSpace = line.lastIndexOf(" ");

            vars.put(line.substring(7), (int) (Math.random() * Integer.parseInt(line.substring(0, lastSpace))));
        } else if (line.matches("call [A-Za-z_]+")) {
            if (labels.containsKey(line.substring(5))) {
                calls.add(currentLine);
                currentLine = labels.get(line.substring(5));
            } else {
                throw new RuntimeException("Label " + line.substring(5) + " não existe.");
            }
        } else if (line.matches("goto [A-Za-z_]+")) {
            if (labels.containsKey(line.substring(5))) {
                currentLine = labels.get(line.substring(5));
            } else {
                throw new RuntimeException("Label " + line.substring(5) + " não existe.");
            }
        } else if (line.matches("if \\(.*\\) then: .*")) {
            if (ExpressionInterpreter.interpret(line.substring(4, line.indexOf(")")), this) != 0) {
                executeLine(line.substring(line.indexOf(")") + 7));
            }
        } else if (line.matches("return")) {
            currentLine = calls.pop();
        } else if (line.matches("array [A-Za-z_]+")) {
            vars.put(line.substring(6), new HashMap<Integer, Object>());
        } else {
            throw new RuntimeException("Arruma o código, erro na linha  \"" + line + "\")");
        }
    }
}
