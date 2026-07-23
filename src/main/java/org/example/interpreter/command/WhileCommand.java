package org.example.interpreter.command;

import org.example.interpreter.Context;
import org.example.interpreter.MiauScriptException;
import org.example.interpreter.expression.Expression;

public class WhileCommand implements Command {
    Expression expression;
    int doneLine;

    public WhileCommand(Expression expression, int doneLine) {
        this.expression = expression;
        this.doneLine = doneLine;
    }

    @Override
    public void execute(Context context) {
        Object expressionObject = expression.evaluate(context.variableManager);

        if (expression.evaluate(context.variableManager) instanceof Double d) {
            if (d == 0.0) {
                context.currentLine = doneLine;
            }

            return;
        }

        throw new MiauScriptException("Erro na expressão do while", expressionObject.toString());
    }
}