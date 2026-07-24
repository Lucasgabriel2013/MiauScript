package org.example.interpreter.command.stop;

import org.example.interpreter.Context;
import org.example.interpreter.MiauScriptException;
import org.example.interpreter.command.Command;
import org.example.interpreter.expression.Expression;

public class SleepCommand implements Command {
    Expression expression;

    public SleepCommand(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void execute(Context context) {
        try {
            Thread.sleep(((Double) expression.evaluate(context.variableManager)).longValue());
        } catch (InterruptedException e) {
            throw new MiauScriptException("Erro no sleep");
        }
    }
}
