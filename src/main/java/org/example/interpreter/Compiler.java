package org.example.interpreter;

import org.example.interpreter.command.*;
import org.example.interpreter.command.blocks.*;
import org.example.interpreter.command.labels.*;
import org.example.interpreter.command.screen.*;
import org.example.interpreter.command.stop.*;
import org.example.interpreter.command.out.*;
import org.example.interpreter.command.variables.*;

import org.example.interpreter.expression.*;

import java.util.*;

public class Compiler {
    ExpressionFactory expressionFactory = new ExpressionFactory();
    public final Stack<Integer> whiles = new Stack<>();

    public Program compile(List<String> lines) {
        List<Command> commands = new ArrayList<>();
        HashMap<String, LabelMetadata> labels = new HashMap<>();
        List<Integer> inits = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            List<String> tokens = splitLine(line);

            if (line.isEmpty()
                    || line.startsWith("#")
                    || line.equals("end")) {

                commands.add(new NothingCommand());
                continue;
            }

            if (line.equals("__init:")) {
                inits.add(i);
                commands.add(new NothingCommand());
                continue;
            }

            if (line.matches("[A-Za-z_]\\w*\\(\\s*([A-Za-z_]\\w*(\\s*,\\s*[A-Za-z_]\\w*)*)?\\s*\\):")) {
                String labelName = line.substring(0, line.indexOf("("));
                List<String> params = splitParams(line.substring(line.indexOf("(") + 1, line.indexOf(")")));

                LabelMetadata label = new LabelMetadata(labelName, i, params);
                labels.put(labelName, label);
                commands.add(new NothingCommand());
                continue;
            }


            if (line.matches("meow \\(.+\\)")) {
                commands.add(new MeowCommand(expressionFactory.interpret(removeParenthesis(tokens.get(1)))));
                continue;
            }

            if (line.matches("purr \\(.+\\)")) {
                commands.add(new PurrCommand(expressionFactory.interpret(removeParenthesis(tokens.get(1)))));
                continue;
            }

            if (line.matches("error \\(.+\\)")) {
                commands.add(new ErrorCommand(expressionFactory.interpret(removeParenthesis(tokens.get(1)))));
                continue;
            }

            if (line.matches("sleep .+")) {
                commands.add(new SleepCommand(expressionFactory.interpret(line.substring(line.indexOf(" ")))));
                continue;
            }

            if (line.matches("keyboard .+")) {
                commands.add(new KeyboardCommand(expressionFactory.interpret(line.substring(line.indexOf(" ")))));
                continue;
            }

            if (line.matches("mouse [A-Za-z_]\\w* = \\(.+\\)")) {
                String varName = tokens.get(1);
                List<String> coordinates = splitParams(removeParenthesis(tokens.get(3)));

                Expression x = expressionFactory.interpret(coordinates.get(0));
                Expression y = expressionFactory.interpret(coordinates.get(1));
                commands.add(new MouseCommand(x, y, varName));
                continue;
            }

            if (line.matches("sound \\(.+\\)")) {
                List<String> soundParams = splitParams(removeParenthesis(tokens.get(1)));

                Expression hz = expressionFactory.interpret(soundParams.get(0));
                Expression msecs = expressionFactory.interpret(soundParams.get(1));
                commands.add(new SoundCommand(hz, msecs));
                continue;
            }

            if (line.matches("object [A-Za-z_]\\w*")) {
                commands.add(new ObjectCommand(tokens.get(1)));
                continue;
            }

            if (line.matches("return( .*)?")) {
                int indexOf = line.indexOf(" ");

                if (indexOf == -1) {
                    commands.add(new ReturnCommand(null));
                    continue;
                }

                commands.add(new ReturnCommand(expressionFactory.interpret(line.substring(indexOf))));
                continue;
            }

            if (line.matches("remove [A-Za-z_]\\w*\\[.*]")) {
                String varName = tokens.get(1).substring(0, tokens.get(1).indexOf("["));
                Expression key = expressionFactory.interpret(tokens.get(1).substring(tokens.get(1).indexOf("[") + 1, tokens.get(1).length() - 1));

                commands.add(new RemoveCommand(varName, key));
                continue;
            }

            if (line.matches("var [A-Za-z_]\\w*\\[.*] = .+")) {
                Expression key = expressionFactory.interpret(tokens.get(1).substring(tokens.get(1).indexOf("[") + 1, tokens.get(1).length() - 1));
                Expression value = expressionFactory.interpret(line.substring(line.indexOf("=") + 2));

                commands.add(new ObjectSetCommand(tokens.get(1).substring(0, tokens.get(1).indexOf("[")), key, value));
                continue;
            }

            if (line.matches("var [A-Za-z_]\\w* = .+")) {
                commands.add(new VarCommand(tokens.get(1), expressionFactory.interpret(line.substring(line.indexOf("=") + 2))));
                continue;
            }

            if (line.matches("global [A-Za-z_]\\w* = .+")) {
                commands.add(new GlobalCommand(tokens.get(1), expressionFactory.interpret(line.substring(line.indexOf("=") + 2))));
                continue;
            }

            if (line.matches("const [A-Za-z_]\\w* = .+")) {
                commands.add(new ConstCommand(tokens.get(1), expressionFactory.interpret(line.substring(line.indexOf("=") + 2))));
                continue;
            }

            if (line.matches("random [A-Za-z_]\\w* = .+")) {
                commands.add(new RandomCommand(tokens.get(1), expressionFactory.interpret(line.substring(line.indexOf("=") + 2))));
                continue;
            }

            if (line.matches("repaint")) {
                commands.add(new RepaintCommand());
                continue;
            }

            if (line.matches("exit")) {
                commands.add(new ExitCommand());
                continue;
            }

            if (line.matches("clear")) {
                commands.add(new ClearCommand());
                continue;
            }

            if (line.matches("done")) {
                commands.add(new DoneCommand(whiles.pop()));
                continue;
            }

            if (line.matches("call [A-Za-z_][A-Za-z0-9_]*\\(.*\\)( : [A-Za-z_][A-Za-z0-9_]*)?")) {
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

            if (line.matches("spawn [A-Za-z_]\\w*")) {
                String labelToken = tokens.get(1);

                commands.add(new SpawnCommand(labelToken));
                continue;
            }

            if (line.matches("setPixel \\(.*\\)")) {
                String[] params = tokens.get(1).substring(1, tokens.get(1).length() - 1).split(",", 5);

                Expression x = expressionFactory.interpret(params[0]);
                Expression y = expressionFactory.interpret(params[1]);
                Expression r = expressionFactory.interpret(params[2]);
                Expression g = expressionFactory.interpret(params[3]);
                Expression b = expressionFactory.interpret(params[4]);

                commands.add(new SetPixelCommand(x, y, r, g, b));
                continue;
            }

            if (line.matches("input [A-Za-z_]\\w*")) {
                commands.add(new InputCommand(tokens.get(1)));
                continue;
            }

            if (line.matches("if \\(.*\\) then:")) {
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

            if (line.matches("if \\(.*\\) then:.*")) {
                Command command = compile(new ArrayList<>(Collections.singleton(line.substring(line.indexOf("then:") + 5))))
                        .commands()
                        .getFirst();
                commands.add(new InlineIfCommand(expressionFactory.interpret(removeParenthesis(tokens.get(1))), command));
                continue;
            }

            if (line.matches("while \\(.*\\) do:")) {
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

            throw new MiauScriptException("Erro na linha: " + line);
        }

        return new Program(commands, labels, inits);
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
