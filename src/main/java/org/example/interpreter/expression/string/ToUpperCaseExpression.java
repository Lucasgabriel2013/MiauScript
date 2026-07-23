package org.example.interpreter.expression.string;

import org.example.interpreter.VariableManager;
import org.example.interpreter.expression.Expression;

public class ToUpperCaseExpression implements Expression {
    String varName;

    public ToUpperCaseExpression(String varName) {
        this.varName = varName;
    }

    @Override
    public Object evaluate(VariableManager variableManager) {
        return varName.toUpperCase();
    }
}
