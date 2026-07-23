package org.example.interpreter.expression.logical;

import org.example.interpreter.MiauScriptException;
import org.example.interpreter.VariableManager;
import org.example.interpreter.expression.Expression;

public class OrExpression implements Expression {
    Expression a;
    Expression b;

    public OrExpression(Expression a, Expression b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public Object evaluate(VariableManager variableManager) {
        Object firstPart = a.evaluate(variableManager);
        Object secondPart = b.evaluate(variableManager);

        if (firstPart instanceof Double d1 && secondPart instanceof Double d2) {
            return (d1 != 0 || d2 != 0)  ? 1.0 : 0.0;
        }

        throw new MiauScriptException("Erro na expressão", firstPart + " e " + secondPart + " deveriam ser números");
    }
}
