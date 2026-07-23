package org.example.interpreter.command;

import org.example.interpreter.Context;

public class InputCommand implements Command {
    String varName;

    public InputCommand(String varName) {
        this.varName = varName;
    }

    @Override
    public void execute(Context context) {
        context.variableManager.setVar(varName, context.console.input());
    }
}