package org.example.interpreter.command.blocks;

import org.example.interpreter.Context;
import org.example.interpreter.MiauScriptException;
import org.example.interpreter.command.Command;
import org.example.interpreter.expression.Expression;

public class IfCommand implements Command {
    Expression expression;
    int endLine;

    public IfCommand(Expression expression, int endLine) {
        this.expression = expression;
        this.endLine = endLine;
    }

    @Override
    public void execute(Context context) {
        Object expressionObject = expression.evaluate(context.variableManager);

        if (expressionObject instanceof Double d) {
            if (d == 0.0) {
                context.currentLine = endLine;
            }

            return;
        }

        throw new MiauScriptException("Erro na expressão do if", expressionObject.toString());
    }
}