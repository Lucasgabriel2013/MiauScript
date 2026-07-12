package org.example.interpreter;

import org.example.screen.Key;

import java.awt.*;
import java.util.*;
import java.util.List;

public class CodeInterpreter {
    private final Queue<Integer> inits = new ArrayDeque<>();
    private final Map<String, LabelMetadata> labels = new HashMap<>();

    private final Stack<Integer> whiles = new Stack<>();
    private final Stack<Integer> calls = new Stack<>();

    public int currentLine;
    private final String[] lines;

    private final VariableManager variableManager = new VariableManager();
    private final ExpressionInterpreter expressionInterpreter = new ExpressionInterpreter(variableManager);

    private final Console console;
    private final DrawablePanel drawablePanel;

    public CodeInterpreter(String[] lines, Console console, DrawablePanel drawablePanel) {
        this.lines = lines;
        this.console = console;
        this.drawablePanel = drawablePanel;

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
        } else {
            currentLine = labels.get("main").line();
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

        if (line.matches("[A-Za-z_][A-Za-z0-9_]*\\([A-Za-z_, ]*\\):")) {
            String name = line.substring(0, line.indexOf("("));

            if (labels.containsKey(name))
                throw new MiauScriptException("Label " + name + " também é declarada em outro lugar");

            String[] params = line.substring(line.indexOf("(") + 1, line.indexOf(")")).split(", ");

            if (params.length == 1 && params[0].isEmpty()) {
                params = new String[0];
            }

            labels.put(name, new LabelMetadata(name, i, params));
        }
    }

    private void executeLine(String line) {
        line = line.trim();

        if (line.isEmpty()
                || line.startsWith(":/")
                || line.matches("[A-Za-z_][A-Za-z0-9_]*\\([A-Za-z_, ]*\\):")
                || line.equals("end")
                || line.equals("__init:"))
            return;

        String lineStart = line.split("\\s+")[0];

        switch (lineStart) {
            case "meow" -> meow(line);
            case "purr" -> purr(line);
            case "error" -> error(line);
            case "sleep" -> sleep(line);
            case "var" -> declare(line);
            case "global" -> global(line);
            case "const" -> consts(line);
            case "random" -> random(line);
            case "call" -> call(line);
            case "if" -> ifStatement(line);
            case "while" -> whileStatement(line);
            case "exit" -> {
                if (!line.equals("exit")) throw new MiauScriptException("Erro no exit:", line);

                System.exit(0);
            }
            case "clear" -> {
                if (!line.equals("clear")) throw new MiauScriptException("Erro no clear:", line);

                console.clear();
            }
            case "return" -> returnStatement(line);
            case "object" -> object(line);
            case "remove" -> remove(line);
            case "input" -> input(line);
            case "keyboard" -> keyboard(line);
            case "done" -> {
                if (!line.equals("done")) throw new MiauScriptException("Erro no done:", line);

                currentLine = whiles.pop() - 1;
            }
            case "setPixel" -> setPixel(line);
            default -> throw new MiauScriptException("Erro na linha:", line);
        }
    }

    private void setPixel(String line) {
        if (!line.matches("setPixel (.*, .*, .*, .*, .*)"))
            throw new MiauScriptException("Erro no setPixel: ", line);

        String[] parts = line.substring(10, line.length() - 1).split(",");

        var a = Arrays.stream(parts)
                .map(expressionInterpreter::interpret)
                .mapToDouble(value -> (Double) value)
                .toArray();

        try {
            drawablePanel.setPixel((int) a[0], (int) a[1], new Color((int) a[2], (int) a[3], (int) a[4]));
        } catch (ArrayIndexOutOfBoundsException _) {
            throw new MiauScriptException("Tentativa de setPixel fora da tela:" + line);
        }
    }

    private void remove(String line) {
        if (!line.matches("remove [A-Za-z_][A-Za-z0-9_]*\\[.*]"))
            throw new MiauScriptException("Erro no remove: ", line);

        String objectName = line.substring(7, line.indexOf("["));
        Object key = line.substring(line.indexOf("[") + 1, line.indexOf("]"));

        key = expressionInterpreter.interpret(key.toString());

        variableManager.getObject(objectName).remove(key);
    }

    private void sleep(String line) {
        if (!line.matches("sleep .*"))
            throw new MiauScriptException("Erro no sleep: ", line);

        try {
            Thread.sleep(((Double) expressionInterpreter.interpret(line.substring(6))).longValue());
        } catch (InterruptedException e) {
            throw new MiauScriptException("Erro no sleep da linha:", line);
        }
    }

    private void returnStatement(String line) {
        if (!line.matches("return") && !line.matches("return .*"))
            throw new MiauScriptException("Erro no return: ", line);

        if (line.length() > 7) {
            String toReturn = line.substring(7);

            variableManager.popFrameReturning(expressionInterpreter.interpret(toReturn));
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
        if (!line.matches("input (number )?[A-Za-z_][A-Za-z0-9_]*"))
            throw new MiauScriptException("Erro na line do input: ", line);

        if (line.matches("input number [A-Za-z_][A-Za-z0-9_]*")) {
            variableManager.setVar(line.substring(13), Double.parseDouble(console.input()));
            return;
        }

        variableManager.setVar(line.substring(6), console.input());
    }

    private void keyboard(String line) {
        if (!line.matches("keyboard (w|a|s|d|enter|space)"))
            throw new MiauScriptException("Erro na line do keyboard: ", line);

        String key = line.substring(9);

        variableManager.setVar(key, console.isPressed(Key.valueOf(key.toUpperCase()))? 1.0 : 0.0);
    }

    private void object(String line) {
        if (!line.matches("object [A-Za-z_][A-Za-z0-9_]*"))
            throw new MiauScriptException("Erro na declaração de object: ", line);

        variableManager.setVar(line.substring(7), new HashMap<>());
    }

    private void ifStatement(String line) {
        if (!line.matches("if \\(.*\\) then:"))
            throw new MiauScriptException("Erro no if: ", line);

        var exp = expressionInterpreter.interpret(line.substring(4, line.indexOf(")")));

        if (!(exp instanceof Double d)) {
            throw new MiauScriptException("Erro na expressão dentro do if:", line);
        }

        if (d == 0) {
            int nestedBlocks = 0;

            while (true) {
                currentLine++;

                line = lines[currentLine].trim();

                if (line.startsWith("if")) {
                    nestedBlocks++;
                    continue;
                }

                if (line.equals("end") && nestedBlocks > 0) {
                    nestedBlocks--;
                    continue;
                }

                if (line.equals("end") && nestedBlocks == 0) break;
            }
        }
    }

    private void whileStatement(String line) {
        if (!line.matches("while \\(.*\\) do:"))
            throw new MiauScriptException("Erro no while: ", line);

        var exp = expressionInterpreter.interpret(line.substring(7, line.indexOf(")")));

        if (!(exp instanceof Double d)) {
            throw new MiauScriptException("Erro na expressão dentro do while:", line);
        }

        if (d != 0) {
            whiles.add(currentLine);
            return;
        }

        int nestedBlocks = 0;

        while (true) {
            currentLine++;

            line = lines[currentLine].trim();

            if (line.startsWith("while")) {
                nestedBlocks++;
                continue;
            }

            if (line.equals("done") && nestedBlocks > 0) {
                nestedBlocks--;
                continue;
            }

            if (line.equals("done") && nestedBlocks == 0) break;
        }
    }

    private void call(String line) {
        if (!line.matches("call [A-Za-z_][A-Za-z0-9_]*\\(.*\\)"))
            throw new MiauScriptException("Erro no call: ", line);

        Object[] params = splitParams(line.substring(line.indexOf("(") + 1, line.indexOf(")")));
        String labelName = line.substring(5, line.indexOf("("));
        LabelMetadata label = labels.get(labelName);

        if (!labels.containsKey(labelName)) {
            throw new MiauScriptException("Label " + labelName + " não existe: ", line);
        }

        HashMap<String, Object> newFrame = new HashMap<>();

        if (label.params() != null) {
            for (int i = 0; i < label.params().length; i++) {
                params[i] = expressionInterpreter.interpret(params[i].toString());

                newFrame.put(label.params()[i], params[i]);
            }
        }

        variableManager.createNewFrame(newFrame);
        calls.add(currentLine);
        currentLine = labels.get(labelName).line();
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

        var exp = expressionInterpreter.interpret(line.substring(lastSpace));

        if (!(exp instanceof Double d)) {
            throw new MiauScriptException("Erro na expressão no random:", line);
        }

        variableManager.setVar(line.substring(7, equalIndex - 1), (double) (int) (Math.random() * d));
    }

    private void declare(String line) {
        if (!line.matches("var .+ = .+"))
            throw new MiauScriptException("Erro na declaração de variável: ", line);

        int equalIndex = line.indexOf("=");

        if (line.matches("var [A-Za-z_][A-Za-z0-9_]*\\[.*] = .+")) {
            Object key = line.substring(line.indexOf("[") + 1, line.indexOf("]"));

            if (key.toString().contains(","))
                throw new MiauScriptException("Valores em objetos não podem conter virgulas");
            key = expressionInterpreter.interpret(key.toString());

            Object value = line.substring(line.indexOf("=") + 2);
            String stringValue = value.toString();

            value = expressionInterpreter.interpret(stringValue);

            HashMap<Object, Object> map = variableManager.getObject(line.substring(4, line.indexOf("[")));

            map.put(key, value);
            return;
        }

        variableManager.setVar(line.substring(4, equalIndex - 1), expressionInterpreter.interpret(line.substring(equalIndex + 2)));
    }

    private void global(String line) {
        if (!line.matches("global .+ = .+"))
            throw new MiauScriptException("Erro na declaração de variável global: ", line);

        int equalIndex = line.indexOf("=");

        variableManager.setGlobalVar(line.substring(7, equalIndex - 1), expressionInterpreter.interpret(line.substring(equalIndex + 2)));
    }

    private void consts(String line) {
        if (!line.matches("const .+ = .+"))
            throw new MiauScriptException("Erro na declaração de constante: ", line);

        int equalIndex = line.indexOf("=");

        variableManager.setConst(line.substring(6, equalIndex - 1), expressionInterpreter.interpret(line.substring(equalIndex + 2)));
    }

    private void purr(String line) {
        if (!line.matches("purr \\(.*\\)"))
            throw new MiauScriptException("Erro no purr: ", line);

        console.print(expressionInterpreter.interpret(line.substring(6, line.length() - 1)));
    }

    private void meow(String line) {
        if (!line.matches("meow \\(.*\\)"))
            throw new MiauScriptException("Erro no meow: ", line);

        console.println(expressionInterpreter.interpret(line.substring(6, line.length() - 1)));
    }

    private void error(String line) {
        if (!line.matches("error \\(.*\\)"))
            throw new MiauScriptException("Erro no error: ", line);

        throw new MiauScriptException((String) expressionInterpreter.interpret(line.substring(7, line.length() - 1)));
    }
}