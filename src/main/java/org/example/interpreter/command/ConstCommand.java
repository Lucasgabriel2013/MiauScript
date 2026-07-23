package org.example.interpreter.command;

import org.example.interpreter.Context;
import org.example.interpreter.expression.Expression;

public class ConstCommand implements Command {
    String varName;
    Expression expression;

    public ConstCommand(String varName, Expression expression) {
        this.varName = varName;
        this.expression = expression;
    }

    @Override
    public void execute(Context context) {
        context.variableManager.setConst(varName, expression.evaluate(context.variableManager));
    }
}
