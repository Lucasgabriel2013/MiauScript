package org.example.interpreter.command.variables;

import org.example.interpreter.Context;
import org.example.interpreter.command.Command;

import java.util.HashMap;

public class ObjectCommand implements Command {
    String varName;

    public ObjectCommand(String varName) {
        this.varName = varName;
    }

    @Override
    public void execute(Context context) {
        context.variableManager.setVar(varName, new HashMap<>());
    }
}
