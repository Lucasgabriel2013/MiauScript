package org.example.interpreter.expression.logical;

import org.example.interpreter.VariableManager;
import org.example.interpreter.expression.Expression;

public class EqualsExpression implements Expression {
    Expression a;
    Expression b;

    public EqualsExpression(Expression a, Expression b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public Object evaluate(VariableManager variableManager) {
        Object firstPart = a.evaluate(variableManager);
        Object secondPart = b.evaluate(variableManager);

        return firstPart.equals(secondPart)? 1.0 : 0.0;
    }
}
