package org.example.interpreter.command;

import org.example.interpreter.Context;
import org.example.interpreter.MiauScriptException;
import org.example.interpreter.expression.Expression;

public class ErrorCommand implements Command {
    Expression expression;

    public ErrorCommand(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void execute(Context context) {
        throw new MiauScriptException((String) expression.evaluate(context.variableManager));
    }
}
