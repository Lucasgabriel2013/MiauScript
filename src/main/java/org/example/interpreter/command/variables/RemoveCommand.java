package org.example.interpreter.command.variables;

import org.example.interpreter.Context;
import org.example.interpreter.command.Command;
import org.example.interpreter.expression.Expression;

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
