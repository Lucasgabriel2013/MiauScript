package org.example.interpreter.command.blocks;

import org.example.interpreter.Context;
import org.example.interpreter.MiauScriptException;
import org.example.interpreter.command.Command;
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

        if (expressionObject instanceof Double d) {
            if (d == 0.0) {
                context.currentLine = doneLine;
            }

            return;
        }

        throw new MiauScriptException("Erro na expressão do while", expressionObject.toString());
    }
}