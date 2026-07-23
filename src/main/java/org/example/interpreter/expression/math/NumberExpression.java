package org.example.interpreter.expression.math;

import org.example.interpreter.VariableManager;
import org.example.interpreter.expression.Expression;

public class NumberExpression implements Expression {
    double num;

    public NumberExpression(double num) {
        this.num = num;
    }

    @Override
    public Object evaluate(VariableManager variableManager) {
        return num;
    }
}
