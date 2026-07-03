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

            processLabels(line, i);
        }

        if (!labels.containsKey("main")) {
            throw new MiauScriptException("Label main não encontrada");
        }

        for (currentLine = labels.get("main").line(); currentLine < lines.length; currentLine++) {
            String line = lines[currentLine];

            executeLine(line);
        }
    }

    private void processLabels(String line, int i) {
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
            case "exit" -> System.exit(0);
            case "return" -> returnStatement(line);
            case "object" -> object(line);
            case "remove" -> remove(line);
            case "input" -> input(line);
            default -> throw new MiauScriptException("Erro na line: ", line);
        }
    }

    private void remove(String line) {
        if (!line.matches("remove [A-Za-z_][A-Za-z0-9_]*\\[.*]"))
            throw new MiauScriptException("Erro no remove: ", line);

        String objectName = line.substring(7, line.indexOf("["));
        Object key = line.substring(line.indexOf("[") + 1, line.indexOf("]"));

        if (key.toString().startsWith("\"") && key.toString().endsWith("\"")) {
            key = key.toString().substring(1, key.toString().length() - 1);
        } else if (vars.peek().containsKey(key)) {
            key = vars.peek().get(key);
        } else {
            key = ExpressionInterpreter.interpret(key.toString(), this);
        }


        @SuppressWarnings("unchecked")
        HashMap<Object, Object> map = (HashMap<Object, Object>) vars.peek().get(objectName);

        map.remove(key);
    }

    private void sleep(String line) {
        if (!line.matches("sleep .*"))
            throw new MiauScriptException("Erro no sleep: ", line);

        try {
            Thread.sleep((long) ExpressionInterpreter.interpret(line.substring(6), this));
        } catch (InterruptedException e) {
            throw new RuntimeException("Erro no sleep da line: \"" + line + "\"");
        }
    }

    private void returnStatement(String line) {
        if (!line.matches("return") && !line.matches("return .*"))
            throw new MiauScriptException("Erro no return: ", line);

        if (line.length() > 7) {
            String toReturn = line.substring(7);

            if (toReturn.startsWith("\"") && toReturn.endsWith("\"")) {
                vars.get(vars.size() - 2).put("result", toReturn.substring(0, toReturn.length() - 1));
            } else if (vars.peek().containsKey(toReturn)) {
                vars.get(vars.size() - 2).put("result", vars.peek().get(toReturn));
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
            throw new MiauScriptException("Erro na line do input: ", line);

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

    private void goTo(String line) {
        if (!line.matches("goto [A-Za-z_][A-Za-z0-9_]*"))
            throw new MiauScriptException("Erro no goto: ", line);

        if (labels.containsKey(line.substring(5))) {
            currentLine = labels.get(line.substring(5)).line();
        } else {
            throw new MiauScriptException("Label " + line.substring(5) + " não existe.");
        }
    }

    private void call(String line) {
        if (!line.matches("call [A-Za-z_][A-Za-z0-9_]*\\(.*\\)"))
            throw new MiauScriptException("Erro no call: ", line);

        Object[] params = splitParams(line.substring(line.indexOf("(") + 1, line.indexOf(")")));
        String labelName = line.substring(5, line.indexOf("("));
        LabelMetadata label = labels.get(labelName);

        if (labels.containsKey(labelName)) {
            HashMap<String, Object> newVars = new HashMap<>();

            if (label.params() != null) {
                for (int i = 0; i < label.params().length; i++) {
                    if (params[i].toString().startsWith("\"") && params[i].toString().endsWith("\"")) {
                        params[i] = params[i].toString().substring(1, params[i].toString().length() - 1);
                    } else if (vars.peek().containsKey(params[i].toString())) {
                        params[i] = vars.peek().get(params[i].toString());
                    } else {
                        params[i] = ExpressionInterpreter.interpret(params[i].toString(), this);
                    }

                    newVars.put(label.params()[i], params[i]);
                }
            }

            vars.add(newVars);
            calls.add(currentLine);
            currentLine = labels.get(labelName).line();
        } else {
            throw new MiauScriptException("Label " + labelName + " não existe: ", line);
        }
    }

    private Object[] splitParams(String text) {
        List<Object> params = new ArrayList<>();
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

        return params.toArray(new Object[0]);
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

        int equalIndex = line.indexOf("=");
        String result = line.substring(equalIndex + 2);

        if (line.matches("var [A-Za-z_][A-Za-z0-9_]* = \".+\"")) {
            vars.peek().put(line.substring(4, equalIndex - 1), result.substring(1, result.length() - 1));
        } else if (line.matches("var [A-Za-z_][A-Za-z0-9_]*\\[.*] = .+")) {
            Object key = line.substring(line.indexOf("[") + 1, line.indexOf("]"));

            if (key.toString().matches("\".*\"")) {
                key = key.toString().substring(1, key.toString().length() - 1);
            } else {
                if (vars.peek().containsKey(key.toString())) {
                    key = getVar((String) key);
                } else {
                    key = ExpressionInterpreter.interpret(key.toString(), this);
                }
            }

            Object value = line.substring(line.indexOf("=") + 2);
            String stringValue = value.toString();

            if (stringValue.startsWith("\"") && stringValue.endsWith("\"")) {
                value = stringValue.substring(1, stringValue.length() - 1);
            } else if (vars.peek().containsKey(value)) {
                value = vars.peek().get(value).toString();
            } else if (stringValue.contains("[") && stringValue.endsWith("]") && vars.peek().containsKey(stringValue.substring(0, stringValue.indexOf("[")))) {
                @SuppressWarnings("unchecked")
                HashMap<Object, Object> map = (HashMap<Object, Object>) getVar(stringValue.substring(0, stringValue.indexOf("[")));
                Object key2 = stringValue.substring(stringValue.indexOf("[") + 1, stringValue.indexOf("]"));

                if (key2.toString().startsWith("\"") && key2.toString().endsWith("\"")) {
                    key2 = key.toString().substring(1, key2.toString().length() - 1);
                } else if (vars.peek().containsKey(key2)) {
                    key2 = vars.peek().get(key2);
                } else {
                    key2 = ExpressionInterpreter.interpret(key2.toString(), this);
                }

                value = map.get(key2);
            } else {
                value = ExpressionInterpreter.interpret(stringValue, this);
            }

            @SuppressWarnings("unchecked")
            HashMap<Object, Object> map = (HashMap<Object, Object>) vars.peek().get(line.substring(4, line.indexOf("[")));

            if (line.matches("var [A-Za-z_][A-Za-z0-9_]*\\[.*] = \".+\"")) {
                value = stringValue.substring(1, line.length() - 1);
                map.put(key, value);
            } else if (line.matches("var [A-Za-z_][A-Za-z0-9_]*\\[.*] = .+")) {
                map.put(key, value);
            }
        } else if (vars.peek().containsKey(result)) {
            vars.peek().put(line.substring(4, equalIndex - 1), vars.peek().get(result));
        } else {
            vars.peek().put(line.substring(4, equalIndex - 1), ExpressionInterpreter.interpret(line.substring(equalIndex + 2), this));
        }
    }

    private void global(String line) {
        if (!line.matches("global .+ = .+"))
            throw new MiauScriptException("Erro na declaração de variável global: ", line);

        int equalIndex = line.indexOf("=");

        if (line.matches("global [A-Za-z_][A-Za-z0-9_]* = \".+\"")) {
            globalVars.put(line.substring(7, equalIndex - 1), line.substring(equalIndex + 3, line.length() - 1));
        } else {
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
            return;
        }

        if (vars.peek().containsKey(line.substring(6, line.length() - 1))) {
            System.out.print(getVar(line.substring(6, line.length() - 1)));
            return;
        }

        if (line.contains("[") && vars.peek().containsKey(line.substring(6, line.indexOf("[")))) {
            @SuppressWarnings("unchecked")
            HashMap<Object, Object> map = (HashMap<Object, Object>) getVar(line.substring(6, line.indexOf("[")));
            Object key = line.substring(line.indexOf("[") + 1, line.indexOf("]"));

            if (key.toString().startsWith("\"") && key.toString().endsWith("\"")) {
                key = key.toString().substring(1, key.toString().length() - 1);
            } else if (vars.peek().containsKey(key)) {
                key = vars.peek().get(key);
            } else {
                key = ExpressionInterpreter.interpret(key.toString(), this);
            }

            System.out.print(map.get(key));
            return;
        }

        System.out.print(ExpressionInterpreter.interpret(line.substring(6, line.length() - 1), this));
    }

    private void meow(String line) {
        if (!line.matches("meow \\(.*\\)"))
            throw new MiauScriptException("Erro no meow: ", line);

        if (line.matches("meow \\(\".*\"\\)")) {
            System.out.println(line.substring(7, line.length() - 2));
            return;
        }

        if (vars.peek().containsKey(line.substring(6, line.length() - 1))) {
            System.out.println(getVar(line.substring(6, line.length() - 1)));
            return;
        }

        if (line.contains("[") && vars.peek().containsKey(line.substring(6, line.indexOf("[")))) {
            @SuppressWarnings("unchecked")
            HashMap<Object, Object> map = (HashMap<Object, Object>) getVar(line.substring(6, line.indexOf("[")));
            Object key = line.substring(line.indexOf("[") + 1, line.indexOf("]"));

            if (key.toString().startsWith("\"") && key.toString().endsWith("\"")) {
                key = key.toString().substring(1, key.toString().length() - 1);
            } else if (vars.peek().containsKey(key)) {
                key = vars.peek().get(key);
            } else {
                key = ExpressionInterpreter.interpret(key.toString(), this);
            }

            System.out.println(map.get(key));
            return;
        }

        System.out.println(ExpressionInterpreter.interpret(line.substring(6, line.length() - 1), this));
    }

    public Object getVar(String exp) {

        if (exp.contains("[\"") && exp.contains("\"]")) {
            @SuppressWarnings("unchecked")
            String s = ((HashMap<Object, Object>) vars.peek().get(exp.substring(0, exp.indexOf("["))))
                    .get(exp.substring(exp.indexOf("[\"") + 2, exp.indexOf("\"]"))).toString();

            return s;
        }

        if (exp.contains("[") && exp.contains("]")) {
            @SuppressWarnings("unchecked")
            String s = ((HashMap<Object, Object>) vars.peek().get(exp.substring(0, exp.indexOf("["))))
                    .get(vars.peek().get(exp.substring(exp.indexOf("[") + 1, exp.indexOf("]")))).toString();

            return s;
        }

        if (consts.containsKey(exp)) {
            return consts.get(exp);
        }
        if (vars.peek().containsKey(exp)) {
            return vars.peek().get(exp);
        }
        if (globalVars.containsKey(exp)) {
            return globalVars.get(exp);
        }

        throw new MiauScriptException("Variável não existente: ", exp);
    }
}