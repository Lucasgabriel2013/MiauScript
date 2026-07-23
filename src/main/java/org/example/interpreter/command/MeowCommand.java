package org.example.interpreter.command;

import org.example.interpreter.Context;
import org.example.interpreter.expression.Expression;

public class MeowCommand implements Command {
    Expression expression;

    public MeowCommand(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void execute(Context context) {
        context.console.println(expression.evaluate(context.variableManager));
    }
}
