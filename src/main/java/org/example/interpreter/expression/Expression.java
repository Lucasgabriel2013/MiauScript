package org.example.interpreter.expression;

import org.example.interpreter.VariableManager;

public interface Expression {
    Object evaluate(VariableManager variableManager);
}
