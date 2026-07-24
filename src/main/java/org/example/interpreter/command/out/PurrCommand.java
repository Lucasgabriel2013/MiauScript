package org.example.interpreter.command.out;

import org.example.interpreter.Context;
import org.example.interpreter.command.Command;
import org.example.interpreter.expression.Expression;

public class PurrCommand implements Command {
    Expression expression;

    public PurrCommand(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void execute(Context context) {
        context.console.print(expression.evaluate(context.variableManager));
    }
}
