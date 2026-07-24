package org.example.interpreter.command.variables;

import org.example.interpreter.Context;
import org.example.interpreter.command.Command;
import org.example.interpreter.expression.Expression;

public class RandomCommand implements Command {
    String varName;
    Expression max;

    public RandomCommand(String varName, Expression max) {
        this.varName = varName;
        this.max = max;
    }

    @Override
    public void execute(Context context) {
        context.variableManager.setVar(varName, (double) (int) (Math.random() * (double) max.evaluate(context.variableManager)));
    }
}
