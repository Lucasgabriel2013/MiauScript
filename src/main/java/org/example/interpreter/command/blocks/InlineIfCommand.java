package org.example.interpreter.command.blocks;

import org.example.interpreter.Context;
import org.example.interpreter.MiauScriptException;
import org.example.interpreter.command.Command;
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

        if (expressionObject instanceof Double d) {
            if (d != 0.0) {
                command.execute(context);
            }

            return;
        }

        throw new MiauScriptException("Erro na expressão do if", expressionObject.toString());
    }
}