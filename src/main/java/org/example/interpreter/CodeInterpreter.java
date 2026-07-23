package org.example.interpreter;

import org.example.interpreter.command.Command;
import org.example.interpreter.expression.ExpressionFactory;

import java.util.*;
import java.util.List;

public class CodeInterpreter {
    private final Context context;
    private final Compiler compiler;

    public CodeInterpreter(List<String> lines, Console console, DrawablePanel drawablePanel) {
        this.context = new Context(console, drawablePanel);
        context.variableManager.createNewFrame(new HashMap<>());

        compiler = new Compiler();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            line = line.trim();

            processLabels(line, i);
        }

        if (!context.labels.containsKey("main")) {
            throw new MiauScriptException("Label main não encontrada");
        }

        if (!context.inits.isEmpty()) {
            context.currentLine = context.inits.remove();
        } else {
            context.currentLine = context.labels.get("main").line();
        }

        List<Command> commands = compiler.compile(lines);

        for (; context.currentLine < lines.size(); context.currentLine++) {
            commands.get(context.currentLine).execute(context);
        }
    }

    private void processLabels(String line, int i) {
        if (line.matches("__init:")) {
            context.inits.add(i);
            return;
        }

        if (line.matches("[A-Za-z_]\\w*\\(\\s*([A-Za-z_]\\w*(\\s*,\\s*[A-Za-z_]\\w*)*)?\\s*\\):")) {
            String name = line.substring(0, line.indexOf("("));

            if (context.labels.containsKey(name))
                throw new MiauScriptException("Label " + name + " declarada mais de uma vez");

            String[] params = line.substring(line.indexOf("(") + 1, line.indexOf(")")).split(", ");

            if (params.length == 1 && params[0].isEmpty()) {
                params = new String[0];
            }

            context.labels.put(name, new LabelMetadata(name, i, params));
        }
    }
}
