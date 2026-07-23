package org.example.interpreter.command;

import org.example.interpreter.Context;
import org.example.interpreter.MiauScriptException;
import org.example.interpreter.expression.Expression;

public class InlineIfCommand implements Command {
    Expression expression;
    Command command;

    public InlineIfCommand(Expression expression, Command command) {
        this.expression = expression;
        this.command = command;
    }

    @Override
    public void execute(Context context) {
        Object expressionObject = expression.evaluate(context.variableManager);

        if (expression.evaluate(context.variableManager) instanceof Double d) {
            if (d != 0.0) {
                command.execute(context);
            }

            return;
        }

        throw new MiauScriptException("Erro na expressão do if", expressionObject.toString());
    }
}