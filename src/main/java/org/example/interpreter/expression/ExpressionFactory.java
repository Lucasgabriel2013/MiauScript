package org.example.interpreter.expression;

import org.example.interpreter.expression.logical.*;
import org.example.interpreter.expression.math.*;
import org.example.interpreter.expression.string.*;
import org.example.interpreter.expression.variables.ObjectExpression;
import org.example.interpreter.expression.variables.VariableExpression;

public class ExpressionFactory {
    public Expression interpret(String exp) {
        exp = exp.trim();

        if (exp.contains(" & ")) {
            String[] parts = exp.split("&", 2);
            return new AndExpression(interpret(parts[0]), interpret(parts[1]));
        }

        if (exp.contains(" | ")) {
            String[] parts = exp.split("\\|", 2);
            return new OrExpression(interpret(parts[0]), interpret(parts[1]));
        }

        if (exp.contains(" == ")) {
            String[] parts = exp.split("==", 2);
            return new EqualsExpression(interpret(parts[0]), interpret(parts[1]));
        }

        if (exp.contains(" != ")) {
            String[] parts = exp.split("!=", 2);
            return new DifferentExpression(interpret(parts[0]), interpret(parts[1]));
        }

        if (exp.contains(" > ")) {
            String[] parts = exp.split(">", 2);
            return new GreaterThanExpression(interpret(parts[0]), interpret(parts[1]));
        }

        if (exp.contains(" < ")) {
            String[] parts = exp.split("<", 2);
            return new LessThanExpression(interpret(parts[0]), interpret(parts[1]));
        }

        if (exp.contains(" >= ")) {
            String[] parts = exp.split(">=", 2);
            return new GreaterThanOrEqualExpression(interpret(parts[0]), interpret(parts[1]));
        }

        if (exp.contains(" <= ")) {
            String[] parts = exp.split("<=", 2);
            return new LessThanOrEqualExpression(interpret(parts[0]), interpret(parts[1]));
        }

        if (exp.contains(" charAt ")) {
            String[] parts = exp.split("charAt", 2);

            return new CharAtExpression(interpret(parts[0]), interpret(parts[1]));
        }

        if (exp.contains(" + ")) {
            String[] parts = exp.split("\\+", 2);

            Expression firstPart = interpret(parts[0]);
            Expression secondPart = interpret(parts[1]);

            return new SumExpression(firstPart, secondPart);
        }

        if (exp.contains(" - ")) {
            String[] parts = exp.split("-", 2);

            Expression firstPart = interpret(parts[0]);
            Expression secondPart = interpret(parts[1]);

            return new SubExpression(firstPart, secondPart);
        }

        if (exp.contains(" * ")) {
            String[] parts = exp.split("\\*", 2);

            Expression firstPart = interpret(parts[0]);
            Expression secondPart = interpret(parts[1]);

            return new MultExpression(firstPart, secondPart);
        }

        if (exp.contains(" / ")) {
            String[] parts = exp.split("/", 2);

            Expression firstPart = interpret(parts[0]);
            Expression secondPart = interpret(parts[1]);

            return new DivideExpression(firstPart, secondPart);
        }

        if (exp.contains(" // ")) {
            String[] parts = exp.split("//", 2);
            return new FloorDivideExpression(interpret(parts[0]), interpret(parts[1]));
        }

        if (exp.contains(" % ")) {
            String[] parts = exp.split("%", 2);
            return new RemainderExpression(interpret(parts[0]), interpret(parts[1]));
        }

        if (exp.contains(" ^ ")) {
            String[] parts = exp.split("\\^", 2);
            return new PowExpression(interpret(parts[0]), interpret(parts[1]));
        }

        if (exp.startsWith("\"") && exp.endsWith("\"") && exp.length() > 1) {
            return new StringExpression(exp.substring(1, exp.length() - 1));
        }

        if (exp.contains("[") && exp.contains("]")) {
            String[] parts = exp.split("\\[", 2);

            return new ObjectExpression(parts[0], interpret(parts[1].substring(0, parts[1].length() - 1)));
        }

        if (exp.endsWith(" length")) {
            return new LengthExpression(interpret(exp.substring(0, exp.length() - 7)));
        }

        if (exp.endsWith(" toLowerCase")) {
            return new ToLowerCaseExpression(exp.substring(0, exp.length() - 12));
        }

        if (exp.endsWith(" toUpperCase")) {
            return new ToUpperCaseExpression(exp.substring(0, exp.length() - 12));
        }

        try {
            return new NumberExpression(Double.parseDouble(exp));
        } catch (NumberFormatException e) {
            return new VariableExpression(exp);
        }
    }
}
