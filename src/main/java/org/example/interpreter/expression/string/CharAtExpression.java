package org.example.interpreter.expression.string;

import org.example.interpreter.MiauScriptException;
import org.example.interpreter.VariableManager;
import org.example.interpreter.expression.Expression;

public class CharAtExpression implements Expression {
    Expression a;
    Expression b;

    public CharAtExpression(Expression a, Expression b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public Object evaluate(VariableManager variableManager) {
        Object firstPart = a.evaluate(variableManager);
        Object secondPart = b.evaluate(variableManager);

        if (firstPart instanceof String d1 && secondPart instanceof Double d2) {
            return String.valueOf(d1.charAt(d2.intValue()));
        }

        throw new MiauScriptException("Erro na expressão", "charAt com valores errados");
    }
}
