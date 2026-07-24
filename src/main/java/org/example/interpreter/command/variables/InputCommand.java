package org.example.interpreter.command.variables;

import org.example.interpreter.Context;
import org.example.interpreter.command.Command;

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