package org.example.interpreter.command;

import org.example.interpreter.Context;
import org.example.interpreter.expression.Expression;

import java.util.HashMap;

public class ObjectSetCommand implements Command {
    String varName;
    Expression key;
    Expression value;

    public ObjectSetCommand(String varName, Expression key, Expression value) {
        this.varName = varName;
        this.key = key;
        this.value = value;
    }

    @Override
    public void execute(Context context) {
        HashMap<Object, Object> map = context.variableManager.getObject(varName);

        map.put(key.evaluate(context.variableManager), value.evaluate(context.variableManager));
    }
}