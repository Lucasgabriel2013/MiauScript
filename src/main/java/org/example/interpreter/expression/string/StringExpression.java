package org.example.interpreter.expression.string;

import org.example.interpreter.VariableManager;
import org.example.interpreter.expression.Expression;

public class StringExpression implements Expression {
    String str;

    public StringExpression(String str) {
        this.str = str;
    }

    @Override
    public Object evaluate(VariableManager variableManager) {
        return str;
    }
}
