package org.example;

import java.util.*;

public class CodeInterpreter {
    Stack<Map<String, Object>> vars = new Stack<>();
    Map<String, Object> globalVars = new HashMap<>();
    Map<String, Object> consts = new HashMap<>();

    Map<String, LabelMetadata> labels = new HashMap<>();

    Stack<Integer> calls = new Stack<>();

    Scanner scanner = new Scanner(System.in);

    int currentLine;

    public CodeInterpreter(String[] lines) {
        vars.add(new HashMap<>());

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            line = line.trim();

            if (line.matches("[A-Za-z_][A-Za-z0-9_]*(\\([A-Za-z_, ]*\\))?:")) {
                boolean haveParams = line.contains("(") && line.contains(")");
                String name = line.substring(0, haveParams ? line.indexOf("(") : line.length() - 1);

                if (labels.containsKey(name))
                    throw new RuntimeException("Label " + name + " também é declarada em outro lugar");

                String[] params = new String[0];

                if (haveParams) {
                    params = line.substring(line.indexOf("(") + 1, line.indexOf(")")).split(", ");

                    if (params.length == 1 && params[0].isEmpty()) {
                        params = new String[0];
                    }
                }

                labels.put(name, new LabelMetadata(name, i, params));
            }
        }

        if (!labels.containsKey("main")) {
            throw new MiauScriptException("Label main não encontrada");
        }

        for (currentLine = labels.get("main").linha(); currentLine < lines.length; currentLine++) {
            String line = lines[currentLine];

            executeLine(line);
        }
    }

    private void executeLine(String line) {
        line = line.trim();

        if (line.isEmpty() || line.startsWith(":/") || line.matches("[A-Za-z_][A-Za-z0-9_]*(\\([A-Za-z_, ]*\\))?:"))
            return;

        String lineStart = line.split("\\s+")[0];

        switch (lineStart) {
            case "meow" -> meow(line);
            case "purr" -> purr(line);
            case "sleep" -> sleep(line);
            case "var" -> declare(line);
            case "global" -> global(line);
            case "const" -> consts(line);
            case "random" -> random(line);
            case "call" -> call(line);
            case "goto" -> goTo(line);
            case "if" -> ifStatement(line);
            case "repeat" -> repeatStatement(line);
            case "exit" -> System.exit(0);
            case "return" -> returnStatement(line);
            case "object" -> object(line);
            case "input" -> input(line);
            default -> throw new MiauScriptException("Erro na linha: ", line);
        }
    }


    private void sleep(String line) {
        if (!line.matches("sleep .*"))
            throw new MiauScriptException("Erro no sleep: ", line);

        try {
            Thread.sleep((long) ExpressionInterpreter.interpret(line.substring(6), this));
        } catch (InterruptedException e) {
            throw new RuntimeException("Erro no sleep da linha: \"" + line + "\"");
        }
    }

    private void returnStatement(String line) {
        if (!line.matches("return") && !line.matches("return .*"))
            throw new MiauScriptException("Erro no return: ", line);

        if (line.length() > 7) {
            String toReturn = line.substring(7);

            if (toReturn.startsWith("\"") && toReturn.endsWith("\"")) {
                vars.get(vars.size() - 2).put("result", toReturn.substring(0, toReturn.length() - 1));
            } else {
                vars.get(vars.size() - 2).put("result", ExpressionInterpreter.interpret(toReturn, this));
            }
        }

        vars.pop();

        if (calls.isEmpty()) System.exit(0);

        currentLine = calls.pop();
    }

    private void input(String line) {
        if (!line.matches("input [A-Za-z_][A-Za-z0-9_]*"))
            throw new MiauScriptException("Erro na linha do input: ", line);

        vars.peek().put(line.substring(6), scanner.nextLine());

    }

    private void object(String line) {
        if (!line.matches("object [A-Za-z_][A-Za-z0-9_]*"))
            throw new MiauScriptException("Erro na declaração de object: ", line);

        vars.peek().put(line.substring(7), new HashMap<>());
    }

    private void ifStatement(String line) {
        if (!line.matches("if \\(.*\\) then: .*"))
            throw new MiauScriptException("Erro no if: ", line);

        if (ExpressionInterpreter.interpret(line.substring(4, line.indexOf(")")), this) != 0) {
            executeLine(line.substring(line.indexOf(")") + 7));
        }
    }

    private void repeatStatement(String line) {
        if (!line.matches("repeat .* times: .*"))
            throw new MiauScriptException("Erro no repeat: ", line);

        String number = line.substring(line.indexOf(" "), line.indexOf("times:", 2));
        double num = ExpressionInterpreter.interpret(number, this);

        for (int i = 0; i < num; i++) {
            executeLine(line.substring(line.indexOf("times:") + 6));
        }
    }

    private void goTo(String line) {
        if (!line.matches("goto [A-Za-z_][A-Za-z0-9_]*"))
            throw new MiauScriptException("Erro no goto: ", line);

        if (labels.containsKey(line.substring(5))) {
            currentLine = labels.get(line.substring(5)).linha();
        } else {
            throw new RuntimeException("Label " + line.substring(5) + " não existe.");
        }
    }

    private void call(String line) {
        if (!line.matches("call [A-Za-z_][A-Za-z0-9_]*\\(.*\\)"))
            throw new MiauScriptException("Erro no call: ", line);

        String[] params = splitParams(line.substring(line.indexOf("(") + 1, line.indexOf(")")));
        String labelName = line.substring(5, line.indexOf("("));
        LabelMetadata label = labels.get(labelName);

        if (labels.containsKey(labelName)) {
            HashMap<String, Object> newVars = new HashMap<>();

            if (label.params() != null) {
                for (int i = 0; i < label.params().length; i++) {
                    if (params[i].startsWith("\"") && params[i].endsWith("\"")) {
                        params[i] = params[i].substring(1, params[i].length() - 1);
                    } else {
                        params[i] = String.valueOf(ExpressionInterpreter.interpret(params[i], this));
                    }

                    newVars.put(label.params()[i], params[i]);
                }
            }

            vars.add(newVars);
            calls.add(currentLine);
            currentLine = labels.get(labelName).linha();
        } else {
            throw new MiauScriptException("Label " + labelName + " não existe: ", line);
        }
    }

    private String[] splitParams(String text) {
        List<String> params = new ArrayList<>();
        boolean insideString = false;
        int lastSeparationIndex = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '\"') {
                insideString = !insideString;
            } else if (c == ',' && !insideString) {
                params.add(text.substring(lastSeparationIndex, i).trim());
                lastSeparationIndex = i + 1;
            }

            if (i == text.length() - 1) {
                params.add(text.substring(lastSeparationIndex, i + 1).trim());
            }
        }

        return params.toArray(new String[0]);
    }

    private void random(String line) {
        if (!line.matches("random [A-Za-z_][A-Za-z0-9_]* = [0-9]+"))
            throw new MiauScriptException("Erro no random: ", line);

        int lastSpace = line.lastIndexOf(" ");
        int equalIndex = line.indexOf("=");

        vars.peek().put(line.substring(7, equalIndex - 1), (int) (Math.random() * ExpressionInterpreter.interpret(line.substring(lastSpace), this)));
    }

    private void declare(String line) {
        if (!line.matches("var .+ = .+"))
            throw new MiauScriptException("Erro na declaração de variável: ", line);

        if (line.matches("var [A-Za-z_][A-Za-z0-9_]* = \".+\"")) {
            int equalIndex = line.indexOf("=");

            vars.peek().put(line.substring(4, equalIndex - 1), line.substring(equalIndex + 3, line.length() - 1));
        } else if (line.matches("var [A-Za-z_][A-Za-z0-9_]*\\[.*] = .+")) {
            String key = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
            String value = line.substring(line.indexOf("=") + 2);

            @SuppressWarnings("unchecked")
            HashMap<Object, Object> map = (HashMap<Object, Object>) vars.peek().get(line.substring(4, line.indexOf("[")));

            if (line.matches("var [A-Za-z_][A-Za-z0-9_]*\\[.*] = \".+\"")) {
                value = value.substring(1, line.length() - 1);
                map.put(key, value);
            } else if (line.matches("var [A-Za-z_][A-Za-z0-9_]*\\[.*] = .+")) {
                map.put(key, value);
            }
        } else {
            int equalIndex = line.indexOf("=");

            vars.peek().put(line.substring(4, equalIndex - 1), ExpressionInterpreter.interpret(line.substring(equalIndex + 2), this));
        }
    }

    private void global(String line) {
        if (!line.matches("global .+ = .+"))
            throw new MiauScriptException("Erro na declaração de variável global: ", line);

        if (line.matches("global [A-Za-z_][A-Za-z0-9_]* = \".+\"")) {
            int equalIndex = line.indexOf("=");

            globalVars.put(line.substring(7, equalIndex - 1), line.substring(equalIndex + 3, line.length() - 1));
        } else {
            int equalIndex = line.indexOf("=");

            globalVars.put(line.substring(7, equalIndex - 1), ExpressionInterpreter.interpret(line.substring(equalIndex + 2), this));
        }
    }

    private void consts(String line) {
        if (!line.matches("const .+ = .+"))
            throw new MiauScriptException("Erro na declaração de constante: ", line);

        int equalIndex = line.indexOf("=");
        if (consts.containsKey(line.substring(6, equalIndex - 1))) throw new MiauScriptException("Tentativa de alterar uma constante: ", line);

        if (line.matches("const [A-Za-z_][A-Za-z0-9_]* = \".+\"")) {
            consts.put(line.substring(6, equalIndex - 1), line.substring(equalIndex + 3, line.length() - 1));
        } else {
            consts.put(line.substring(6, equalIndex - 1), ExpressionInterpreter.interpret(line.substring(equalIndex + 2), this));
        }
    }

    private void purr(String line) {
        if (!line.matches("purr \\(.*\\)"))
            throw new MiauScriptException("Erro no purr: ", line);

        if (line.matches("purr \\(\".*\"\\)")) {
            System.out.print(line.substring(7, line.length() - 2));
        } else {
            try {
                System.out.print(ExpressionInterpreter.interpret(line.substring(6, line.length() - 1), this));
            } catch (RuntimeException e) {
                System.out.println(getVar(line.substring(6, line.length() - 1)));
            }
        }
    }

    private void meow(String line) {
        if (!line.matches("meow \\(.*\\)"))
            throw new MiauScriptException("Erro no meow: ", line);

        if (line.matches("meow \\(\".*\"\\)")) {
            System.out.println(line.substring(7, line.length() - 2));
        } else {
            try {
                System.out.println(ExpressionInterpreter.interpret(line.substring(6, line.length() - 1), this));
            } catch (RuntimeException e) {
                System.out.println(getVar(line.substring(6, line.length() - 1)));
            }
        }
    }

    public String getVar(String exp) {
        try {
            if (exp.contains("[") && exp.contains("]")) {
                @SuppressWarnings("unchecked")
                String s = ((HashMap<Object, Object>) vars.peek().get(exp.substring(0, exp.indexOf("["))))
                        .get(exp.substring(exp.indexOf("[") + 1, exp.indexOf("]"))).toString();

                return s;
            }
        } catch (NumberFormatException _) {}

        if (consts.containsKey(exp)) {
            return consts.get(exp).toString();
        } else if (vars.peek().containsKey(exp)) {
            return vars.peek().get(exp).toString();
        } else if (globalVars.containsKey(exp)) {
            return globalVars.get(exp).toString();
        } else {
            throw new MiauScriptException("Variável não existente: ", exp);
        }
    }
}