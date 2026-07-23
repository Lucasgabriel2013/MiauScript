package org.example.interpreter.command;

import org.example.interpreter.Context;
import org.example.interpreter.expression.Expression;

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
