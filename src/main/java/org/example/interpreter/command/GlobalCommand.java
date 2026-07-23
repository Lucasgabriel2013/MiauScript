package org.example.interpreter.command;

import org.example.interpreter.Context;
import org.example.interpreter.expression.Expression;

public class GlobalCommand implements Command {
    String varName;
    Expression expression;

    public GlobalCommand(String varName, Expression expression) {
        this.varName = varName;
        this.expression = expression;
    }

    @Override
    public void execute(Context context) {
        context.variableManager.setGlobalVar(varName, expression.evaluate(context.variableManager));
    }
}
