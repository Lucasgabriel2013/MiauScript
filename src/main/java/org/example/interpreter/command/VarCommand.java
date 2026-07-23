package org.example.interpreter.command;

import org.example.interpreter.Context;
import org.example.interpreter.expression.Expression;

public class VarCommand implements Command {
    String varName;
    Expression expression;

    public VarCommand(String varName, Expression expression) {
        this.varName = varName;
        this.expression = expression;
    }

    @Override
    public void execute(Context context) {
        context.variableManager.setVar(varName, expression.evaluate(context.variableManager));
    }
}
