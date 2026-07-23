package org.example.interpreter.expression.variables;

import org.example.interpreter.VariableManager;
import org.example.interpreter.expression.Expression;

public class ObjectExpression implements Expression {
    String objectName;
    Expression value;

    public ObjectExpression(String objectName, Expression value) {
        this.objectName = objectName;
        this.value = value;
    }

    @Override
    public Object evaluate(VariableManager variableManager) {
        Object secondPart = value.evaluate(variableManager);

        return variableManager.getObject(objectName).get(secondPart);
    }
}
