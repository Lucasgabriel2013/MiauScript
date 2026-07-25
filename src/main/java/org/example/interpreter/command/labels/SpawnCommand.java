package org.example.interpreter.command.labels;

import org.example.interpreter.*;
import org.example.interpreter.command.Command;

public class SpawnCommand implements Command {
    String labelName;

    public SpawnCommand(String labelName) {
        this.labelName = labelName;
    }

    @Override
    public void execute(Context context) {
        if (!context.program.labels().containsKey(labelName)) {
            throw new MiauScriptException("Label " + labelName + " não existe");
        }

        LabelMetadata label = context.program.labels().get(labelName);
        Context threadContext = new Context(context.console, context.drawablePanel, context.program, false);
        threadContext.variableManager = new VariableManager(context.variableManager.getGlobalVars(), context.variableManager.getConsts());
        threadContext.currentLine = label.line();
        threadContext.inits.clear();

        Thread.startVirtualThread(() -> new CodeInterpreter(threadContext, labelName));
    }
}
