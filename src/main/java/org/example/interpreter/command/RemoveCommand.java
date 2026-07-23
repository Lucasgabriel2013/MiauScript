package org.example.interpreter.command;

import org.example.interpreter.Context;
import org.example.interpreter.expression.Expression;
import org.example.interpreter.expression.variables.ObjectExpression;

import java.util.HashMap;

public class RemoveCommand implements Command {
    String objectName;
    Expression key;

    public RemoveCommand(String objectName, Expression key) {
        this.objectName = objectName;
        this.key = key;
    }

    @Override
    public void execute(Context context) {
        context.variableManager.getObject(objectName).remove(key.evaluate(context.variableManager));
    }
}
