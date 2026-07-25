package org.example.interpreter;

import java.util.*;

public class CodeInterpreter {
    public CodeInterpreter(Context context, String labelName) {
        context.variableManager.createNewFrame(new HashMap<>());
        Program program = context.program;

        context.currentLine = program.labels().get(labelName).line();

        if (labelName.equals("main")) {
            processInits(context, program);
        }

        for (; context.currentLine < program.commands().size(); context.currentLine++) {
            if (context.threadStopped) {
                return;
            }

            program.commands().get(context.currentLine).execute(context);
        }
    }

    private static void processInits(Context context, Program program) {
        Queue<Integer> inits = context.inits;

        if (!program.labels().containsKey("main")) {
            throw new MiauScriptException("Label main não encontrada");
        }

        if (!inits.isEmpty()) {
            context.currentLine = inits.remove();
        }
    }
}
