package org.example.interpreter.expression.string;

import org.example.interpreter.VariableManager;
import org.example.interpreter.expression.Expression;

public class LengthExpression implements Expression {
    Expression a;

    public LengthExpression(Expression a) {
        this.a = a;
    }

    @Override
    public Object evaluate(VariableManager variableManager) {
        return (double) a.evaluate(variableManager).toString().length();
    }
}
