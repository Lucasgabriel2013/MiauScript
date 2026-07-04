package org.example;

import java.util.*;

public class CodeInterpreter {
    private final Queue<Integer> inits = new ArrayDeque<>();
    private final Map<String, LabelMetadata> labels = new HashMap<>();

    private final Stack<Integer> calls = new Stack<>();

    private final Scanner scanner = new Scanner(System.in);

    public int currentLine;
    private final String[] lines;

    private final VariableManager variableManager = new VariableManager();

    public CodeInterpreter(String[] lines) {
        this.lines = lines;

        variableManager.createNewFrame(new HashMap<>());

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            line = line.trim();

            processLabels(line, i);
        }

        if (!labels.containsKey("main")) {
            throw new MiauScriptException("Label main não encontrada");
        }

        if (!inits.isEmpty()) {
            currentLine = inits.remove();
        }

        for (; currentLine < lines.length; currentLine++) {
            String line = lines[currentLine];

            executeLine(line);
        }
    }

    private void processLabels(String line, int i) {
        if (line.matches("__init:")) {
            inits.add(i);
            return;
        }

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

        if (line.isEmpty()
                || line.startsWith(":/")
                || line.matches("[A-Za-z_][A-Za-z0-9_]*(\\([A-Za-z_, ]*\\))?:")
                || line.equals("end"))
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
            default -> throw new MiauScriptException("Erro na linha:", line);
        }
    }

    private void remove(String line) {
        if (!line.matches("remove [A-Za-z_][A-Za-z0-9_]*\\[.*]"))
            throw new MiauScriptException("Erro no remove: ", line);

        String objectName = line.substring(7, line.indexOf("["));
        Object key = line.substring(line.indexOf("[") + 1, line.indexOf("]"));

        if (key.toString().startsWith("\"") && key.toString().endsWith("\"")) {
            key = key.toString().substring(1, key.toString().length() - 1);
        } else if (variableManager.isDeclared(objectName)) {
            key = getVar(key.toString());
        } else {
            key = ExpressionInterpreter.interpret(key.toString(), variableManager);
        }

        variableManager.getObject(objectName).remove(key);
    }

    private void sleep(String line) {
        if (!line.matches("sleep .*"))
            throw new MiauScriptException("Erro no sleep: ", line);

        try {
            Thread.sleep((long) ExpressionInterpreter.interpret(line.substring(6), variableManager));
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
                variableManager.popFrameReturning(toReturn.substring(0, toReturn.length() - 1));
            } else if (variableManager.isDeclared(toReturn)) {
                variableManager.popFrameReturning(variableManager.getVar(toReturn));
            } else {
                variableManager.popFrameReturning(ExpressionInterpreter.interpret(toReturn, variableManager));
            }
        } else {
            variableManager.popFrame();
        }

        if (calls.isEmpty() && inits.isEmpty()) {
            currentLine = labels.get("main").line();
            variableManager.createNewFrame(new HashMap<>());
            return;
        }

        if (calls.isEmpty()) {
            currentLine = inits.remove();
            variableManager.createNewFrame(new HashMap<>());
            return;
        }

        currentLine = calls.pop();
    }

    private void input(String line) {
        if (!line.matches("input [A-Za-z_][A-Za-z0-9_]*"))
            throw new MiauScriptException("Erro na line do input: ", line);

        variableManager.setVar(line.substring(6), scanner.nextLine());
    }

    private void object(String line) {
        if (!line.matches("object [A-Za-z_][A-Za-z0-9_]*"))
            throw new MiauScriptException("Erro na declaração de object: ", line);

        variableManager.setVar(line.substring(7), new HashMap<>());
    }

    private void ifStatement(String line) {
        if (!line.matches("if \\(.*\\) then:"))
            throw new MiauScriptException("Erro no if: ", line);

        if (ExpressionInterpreter.interpret(line.substring(4, line.indexOf(")")), variableManager) == 0) {
            int nestedBlocks = 0;

            while (true) {
                currentLine++;

                line = lines[currentLine].trim();

                if (line.startsWith("if") || line.startsWith("while")) {
                    nestedBlocks++;
                    continue;
                }

                if (line.equals("end") && nestedBlocks > 0) {
                    nestedBlocks--;
                    continue;
                }

                if (line.equals("end")  && nestedBlocks == 0) break;
            }
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
            HashMap<String, Object> newFrame = new HashMap<>();

            if (label.params() != null) {
                for (int i = 0; i < label.params().length; i++) {
                    if (params[i].toString().startsWith("\"") && params[i].toString().endsWith("\"")) {
                        params[i] = params[i].toString().substring(1, params[i].toString().length() - 1);
                    } else if (variableManager.isDeclared(params[i].toString())) {
                        params[i] = variableManager.getVar(params[i].toString());
                    } else {
                        params[i] = ExpressionInterpreter.interpret(params[i].toString(), variableManager);
                    }

                    newFrame.put(label.params()[i], params[i]);
                }
            }

            variableManager.createNewFrame(newFrame);
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

        variableManager.setVar(line.substring(7, equalIndex - 1), (int) (Math.random() * ExpressionInterpreter.interpret(line.substring(lastSpace), variableManager)));
    }

    private void declare(String line) {
        if (!line.matches("var .+ = .+"))
            throw new MiauScriptException("Erro na declaração de variável: ", line);

        int equalIndex = line.indexOf("=");
        String result = line.substring(equalIndex + 2);

        if (line.matches("var [A-Za-z_][A-Za-z0-9_]* = \".+\"")) {
            variableManager.setVar(line.substring(4, equalIndex - 1), result.substring(1, result.length() - 1));
        } else if (line.matches("var [A-Za-z_][A-Za-z0-9_]*\\[.*] = .+")) {
            Object key = line.substring(line.indexOf("[") + 1, line.indexOf("]"));

            if (key.toString().matches("\".*\"")) {
                key = key.toString().substring(1, key.toString().length() - 1);
            } else {
                if (variableManager.isDeclared(key.toString())) {
                    key = getVar((String) key);
                } else {
                    key = ExpressionInterpreter.interpret(key.toString(), variableManager);
                }
            }

            Object value = line.substring(line.indexOf("=") + 2);
            String stringValue = value.toString();

            if (stringValue.startsWith("\"") && stringValue.endsWith("\"")) {
                value = stringValue.substring(1, stringValue.length() - 1);
            } else if (variableManager.isDeclared((String) value)) {
                value = variableManager.getVar((String) value);
            } else if (stringValue.contains("[") && stringValue.endsWith("]") && variableManager.isDeclared(stringValue.substring(0, stringValue.indexOf("[")))) {
                @SuppressWarnings("unchecked")
                HashMap<Object, Object> map = (HashMap<Object, Object>) getVar(stringValue.substring(0, stringValue.indexOf("[")));
                Object key2 = stringValue.substring(stringValue.indexOf("[") + 1, stringValue.indexOf("]"));

                if (key2.toString().startsWith("\"") && key2.toString().endsWith("\"")) {
                    key2 = key.toString().substring(1, key2.toString().length() - 1);
                } else if (variableManager.isDeclared((String) key2)) {
                    key2 = variableManager.getVar((String) key2);
                } else {
                    key2 = ExpressionInterpreter.interpret(key2.toString(), variableManager);
                }

                value = map.get(key2);
            } else {
                value = ExpressionInterpreter.interpret(stringValue, variableManager);
            }

            HashMap<Object, Object> map = variableManager.getObject(line.substring(4, line.indexOf("[")));

            if (line.matches("var [A-Za-z_][A-Za-z0-9_]*\\[.*] = \".+\"")) {
                value = stringValue.substring(1, line.length() - 1);
                map.put(key, value);
            } else if (line.matches("var [A-Za-z_][A-Za-z0-9_]*\\[.*] = .+")) {
                map.put(key, value);
            }
        } else if (variableManager.isDeclared(result)) {
            variableManager.setVar(line.substring(4, equalIndex - 1), variableManager.getVar(result));
        } else {
            variableManager.setVar(line.substring(4, equalIndex - 1), ExpressionInterpreter.interpret(line.substring(equalIndex + 2), variableManager));
        }
    }

    private void global(String line) {
        if (!line.matches("global .+ = .+"))
            throw new MiauScriptException("Erro na declaração de variável global: ", line);

        int equalIndex = line.indexOf("=");

        if (line.matches("global [A-Za-z_][A-Za-z0-9_]* = \".+\"")) {
            variableManager.setGlobalVar(line.substring(7, equalIndex - 1), line.substring(equalIndex + 3, line.length() - 1));
        } else {
            variableManager.setGlobalVar(line.substring(7, equalIndex - 1), ExpressionInterpreter.interpret(line.substring(equalIndex + 2), variableManager));
        }
    }

    private void consts(String line) {
        if (!line.matches("const .+ = .+"))
            throw new MiauScriptException("Erro na declaração de constante: ", line);

        int equalIndex = line.indexOf("=");

        if (line.matches("const [A-Za-z_][A-Za-z0-9_]* = \".+\"")) {
            variableManager.setConst(line.substring(6, equalIndex - 1), line.substring(equalIndex + 3, line.length() - 1));
        } else {
            variableManager.setConst(line.substring(6, equalIndex - 1), ExpressionInterpreter.interpret(line.substring(equalIndex + 2), variableManager));
        }
    }

    private void purr(String line) {
        if (!line.matches("purr \\(.*\\)"))
            throw new MiauScriptException("Erro no purr: ", line);

        if (line.matches("purr \\(\".*\"\\)")) {
            System.out.print(line.substring(7, line.length() - 2));
            return;
        }

        if (variableManager.isDeclared(line.substring(6, line.length() - 1))) {
            System.out.print(getVar(line.substring(6, line.length() - 1)));
            return;
        }

        if (line.contains("[") && variableManager.isDeclared(line.substring(6, line.indexOf("[")))) {
            HashMap<Object, Object> map = variableManager.getObject(line.substring(6, line.indexOf("[")));
            Object key = line.substring(line.indexOf("[") + 1, line.indexOf("]"));

            if (key.toString().startsWith("\"") && key.toString().endsWith("\"")) {
                key = key.toString().substring(1, key.toString().length() - 1);
            } else if (variableManager.isDeclared((String) key)) {
                key = variableManager.getVar((String) key);
            } else {
                key = ExpressionInterpreter.interpret(key.toString(), variableManager);
            }

            System.out.print(map.get(key));
            return;
        }

        System.out.print(ExpressionInterpreter.interpret(line.substring(6, line.length() - 1), variableManager));
    }

    private void meow(String line) {
        if (!line.matches("meow \\(.*\\)"))
            throw new MiauScriptException("Erro no meow: ", line);

        if (line.matches("meow \\(\".*\"\\)")) {
            System.out.println(line.substring(7, line.length() - 2));
            return;
        }

        if (variableManager.isDeclared(line.substring(6, line.length() - 1))) {
            System.out.println(getVar(line.substring(6, line.length() - 1)));
            return;
        }

        if (line.contains("[") && variableManager.isDeclared(line.substring(6, line.indexOf("[")))) {
            HashMap<Object, Object> map = variableManager.getObject(line.substring(6, line.indexOf("[")));
            Object key = line.substring(line.indexOf("[") + 1, line.indexOf("]"));

            if (key.toString().startsWith("\"") && key.toString().endsWith("\"")) {
                key = key.toString().substring(1, key.toString().length() - 1);
            } else if (variableManager.isDeclared((String) key)) {
                key = variableManager.getVar((String) key);
            } else {
                key = ExpressionInterpreter.interpret(key.toString(), variableManager);
            }

            System.out.println(map.get(key));
            return;
        }

        System.out.println(ExpressionInterpreter.interpret(line.substring(6, line.length() - 1), variableManager));
    }

    public Object getVar(String exp) {
        if (exp.contains("[\"") && exp.contains("\"]")) {
            return variableManager.getObject(exp.substring(0, exp.indexOf("[")))
                    .get(exp.substring(exp.indexOf("[\"") + 2, exp.indexOf("\"]"))).toString();
        }

        if (exp.contains("[") && exp.contains("]")) {
            return variableManager.getObject(exp.substring(0, exp.indexOf("[")))
                    .get(variableManager.getVar(exp.substring(exp.indexOf("[") + 1, exp.indexOf("]")))).toString();
        }

        return variableManager.getVar(exp);
    }
}