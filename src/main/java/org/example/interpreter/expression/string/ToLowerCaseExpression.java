package org.example.interpreter.expression.string;

import org.example.interpreter.VariableManager;
import org.example.interpreter.expression.Expression;

public class ToLowerCaseExpression implements Expression {
    String varName;

    public ToLowerCaseExpression(String varName) {
        this.varName = varName;
    }

    @Override
    public Object evaluate(VariableManager variableManager) {
        return varName.toLowerCase();
    }
}
