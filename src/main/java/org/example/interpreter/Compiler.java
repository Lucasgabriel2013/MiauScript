package org.example.interpreter;

import org.example.interpreter.command.*;
import org.example.interpreter.command.blocks.*;
import org.example.interpreter.command.labels.*;
import org.example.interpreter.command.screen.*;
import org.example.interpreter.command.stop.*;
import org.example.interpreter.command.out.*;
import org.example.interpreter.command.variables.*;

import org.example.interpreter.expression.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class Compiler {
    ExpressionFactory expressionFactory = new ExpressionFactory();
    public final Stack<Integer> whiles = new Stack<>();

    public List<Command> compile(List<String> lines) {
        List<Command> commands = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            List<String> tokens = splitLine(line);

            if (line.isEmpty()
                    || line.startsWith("#")
                    || line.matches("[A-Za-z_]\\w*\\(\\s*([A-Za-z_]\\w*(\\s*,\\s*[A-Za-z_]\\w*)*)?\\s*\\):")
                    || line.equals("end")
                    || line.equals("__init:")) {

                commands.add(new NothingCommand());
                continue;
            }

            String firstToken = tokens.getFirst();

            if (firstToken.equals("meow") && tokens.size() == 2) {
                commands.add(new MeowCommand(expressionFactory.interpret(removeParenthesis(tokens.get(1)))));
                continue;
            }

            if (firstToken.equals("purr") && tokens.size() == 2) {
                commands.add(new PurrCommand(expressionFactory.interpret(removeParenthesis(tokens.get(1)))));
                continue;
            }

            if (firstToken.equals("error") && tokens.size() == 2) {
                commands.add(new ErrorCommand(expressionFactory.interpret(removeParenthesis(tokens.get(1)))));
                continue;
            }

            if (firstToken.equals("sleep") && tokens.size() == 2) {
                commands.add(new SleepCommand(expressionFactory.interpret(line.substring(line.indexOf(" ")))));
                continue;
            }

            if (firstToken.equals("keyboard")) {
                commands.add(new KeyboardCommand(expressionFactory.interpret(line.substring(line.indexOf(" ")))));
                continue;
            }

            if (firstToken.equals("mouse") && tokens.size() == 4) {
                String varName = tokens.get(1);
                List<String> coordinates = splitParams(removeParenthesis(tokens.get(3)));

                Expression x = expressionFactory.interpret(coordinates.get(0));
                Expression y = expressionFactory.interpret(coordinates.get(1));
                commands.add(new MouseCommand(x, y, varName));
                continue;
            }

            if (firstToken.equals("sound") && tokens.size() == 2) {
                List<String> soundParams = splitParams(removeParenthesis(tokens.get(1)));

                Expression hz = expressionFactory.interpret(soundParams.get(0));
                Expression msecs = expressionFactory.interpret(soundParams.get(1));
                commands.add(new SoundCommand(hz, msecs));
                continue;
            }

            if (firstToken.equals("object") && tokens.size() == 2) {
                commands.add(new ObjectCommand(tokens.get(1)));
                continue;
            }

            if (firstToken.equals("return")) {
                int indexOf = line.indexOf(" ");

                if (indexOf == -1) {
                    commands.add(new ReturnCommand(null));
                    continue;
                }

                commands.add(new ReturnCommand(expressionFactory.interpret(line.substring(indexOf))));
                continue;
            }

            if (firstToken.equals("remove") && tokens.size() == 2) {
                String varName = tokens.get(1).substring(0, tokens.get(1).indexOf("["));
                Expression key = expressionFactory.interpret(tokens.get(1).substring(tokens.get(1).indexOf("[") + 1, tokens.get(1).length() - 1));

                commands.add(new RemoveCommand(varName, key));
                continue;
            }

            if (line.matches("var [A-Za-z_][A-Za-z0-9_]*\\[.*] = .+")) {
                Expression key = expressionFactory.interpret(tokens.get(1).substring(tokens.get(1).indexOf("[") + 1, tokens.get(1).length() - 1));
                Expression value = expressionFactory.interpret(line.substring(line.indexOf("=") + 2));

                commands.add(new ObjectSetCommand(tokens.get(1).substring(0, tokens.get(1).indexOf("[")), key, value));
                continue;
            }

            if (firstToken.equals("var")) {
                commands.add(new VarCommand(tokens.get(1), expressionFactory.interpret(line.substring(line.indexOf("=") + 2))));
                continue;
            }

            if (firstToken.equals("global")) {
                commands.add(new GlobalCommand(tokens.get(1), expressionFactory.interpret(line.substring(line.indexOf("=") + 2))));
                continue;
            }

            if (firstToken.equals("const")) {
                commands.add(new ConstCommand(tokens.get(1), expressionFactory.interpret(line.substring(line.indexOf("=") + 2))));
                continue;
            }

            if (firstToken.equals("random")) {
                commands.add(new RandomCommand(tokens.get(1), expressionFactory.interpret(line.substring(line.indexOf("=") + 2))));
                continue;
            }

            if (firstToken.equals("repaint") && tokens.size() == 1) {
                commands.add(new RepaintCommand());
                continue;
            }

            if (firstToken.equals("exit") && tokens.size() == 1) {
                commands.add(new ExitCommand());
                continue;
            }

            if (firstToken.equals("clear") && tokens.size() == 1) {
                commands.add(new ClearCommand());
                continue;
            }

            if (firstToken.equals("done") && tokens.size() == 1) {
                commands.add(new DoneCommand(whiles.pop()));
                continue;
            }

            if (firstToken.equals("call")) {
                String labelToken = tokens.get(1);
                String paramsStr = labelToken.substring(labelToken.indexOf("(") + 1, labelToken.length() - 1);

                String varName = labelToken.substring(0, labelToken.indexOf("("));
                List<Expression> params = splitParams(paramsStr)
                        .stream()
                        .map(g -> expressionFactory.interpret(g))
                        .toList();

                commands.add(new CallCommand(varName, params, tokens.size() == 4? tokens.get(3) : ""));
                continue;
            }

            if (firstToken.equals("setPixel") && tokens.size() == 2) {
                String[] params = tokens.get(1).substring(1, tokens.get(1).length() - 1).split(",", 5);

                Expression x = expressionFactory.interpret(params[0]);
                Expression y = expressionFactory.interpret(params[1]);
                Expression r = expressionFactory.interpret(params[2]);
                Expression g = expressionFactory.interpret(params[3]);
                Expression b = expressionFactory.interpret(params[4]);

                commands.add(new SetPixelCommand(x, y, r, g, b));
                continue;
            }

            if (firstToken.equals("input") && tokens.size() == 2) {
                commands.add(new InputCommand(tokens.get(1)));
                continue;
            }

            if (firstToken.equals("if") && tokens.getLast().equals("then:") && tokens.size() == 3) {
                int nestedBlocks = 0;
                int endLine = i;

                while (true) {
                    endLine++;

                    line = lines.get(endLine).trim();

                    if (line.startsWith("if") && line.endsWith("then:")) {
                        nestedBlocks++;
                        continue;
                    }

                    if (line.equals("end") && nestedBlocks > 0) {
                        nestedBlocks--;
                        continue;
                    }

                    if (line.equals("end") && nestedBlocks == 0) break;
                }

                commands.add(new IfCommand(expressionFactory.interpret(removeParenthesis(tokens.get(1))), endLine));
                continue;
            }

            if (firstToken.equals("if")) {
                Command command = compile(new ArrayList<>(Collections.singleton(line.substring(line.indexOf("then:") + 5)))).getFirst();
                commands.add(new InlineIfCommand(expressionFactory.interpret(removeParenthesis(tokens.get(1))), command));
                continue;
            }

            if (firstToken.equals("while") && tokens.size() == 3) {
                int nestedBlocks = 0;
                int doneLine = i;

                while (true) {
                    doneLine++;

                    line = lines.get(doneLine).trim();

                    if (line.startsWith("while") && line.endsWith("do:")) {
                        nestedBlocks++;
                        continue;
                    }

                    if (line.equals("done") && nestedBlocks > 0) {
                        nestedBlocks--;
                        continue;
                    }

                    if (line.equals("done") && nestedBlocks == 0) break;
                }

                whiles.push(i);
                commands.add(new WhileCommand(expressionFactory.interpret(removeParenthesis(tokens.get(1))), doneLine));
                continue;
            }

            throw new MiauScriptException("Erro na linha", line);
        }

        return commands;
    }

    private List<String> splitLine(String line) {
        List<String> tokens = new ArrayList<>();
        boolean insideString = false;
        boolean insideParenthesis = false;
        int lastSeparationIndex = 0;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '\"') {
                insideString = !insideString;
            } else if (!insideString && !insideParenthesis && c == '(') {
                insideParenthesis = true;
            } else if (!insideString && insideParenthesis && c == ')') {
                insideParenthesis = false;
            } else if (c == ' ' && !insideString && !insideParenthesis && line.charAt(i - 1) != ' ') {
                tokens.add(line.substring(lastSeparationIndex, i).trim());
                lastSeparationIndex = i + 1;
            } else if (c == '#' && !insideString) {
                return tokens;
            }

            if (i == line.length() - 1) {
                tokens.add(line.substring(lastSeparationIndex, i + 1).trim());
            }
        }

        return tokens;
    }

    private List<String> splitParams(String text) {
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

        return params;
    }

    private static String removeParenthesis(String s) {
        if (s.startsWith("(") && s.endsWith(")")) {
            return s.substring(1, s.length() - 1);
        }

        throw new MiauScriptException("Erro na expressão", s);
    }
}
