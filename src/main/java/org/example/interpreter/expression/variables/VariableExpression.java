package org.example.interpreter.expression.variables;

import org.example.interpreter.MiauScriptException;
import org.example.interpreter.VariableManager;
import org.example.interpreter.expression.Expression;

public class VariableExpression implements Expression {
    private final String varName;

    public VariableExpression(String varName) {
        this.varName = varName;
    }

    @Override
    public Object evaluate(VariableManager variableManager) {
        if (variableManager.isDeclared(varName)) {
            return variableManager.getVar(varName);
        }

        throw new MiauScriptException("Variável não existente", varName);
    }
}
