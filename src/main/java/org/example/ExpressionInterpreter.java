package org.example;

public class ExpressionInterpreter {
    static double interpret(String exp, CodeInterpreter ci) {
        exp = exp.trim();

        if (exp.contains(" & ")) {
            String[] parts = exp.split("&", 2);
            return (interpret(parts[0], ci) == 1) && (interpret(parts[1], ci) == 1)? 1 : 0;
        }

        if (exp.contains(" | ")) {
            String[] parts = exp.split("\\|", 2);
            return (interpret(parts[0], ci) == 1) || (interpret(parts[1], ci) == 1)? 1 : 0;
        }

        if (exp.contains(" == ")) {
            String[] parts = exp.split("==", 2);
            return interpret(parts[0], ci) == interpret(parts[1], ci)? 1 : 0;
        }

        if (exp.contains(" != ")) {
            String[] parts = exp.split("!=", 2);
            return interpret(parts[0], ci) != interpret(parts[1], ci)? 1 : 0;
        }

        if (exp.contains(" > ")) {
            String[] parts = exp.split(">", 2);
            return interpret(parts[0], ci) > interpret(parts[1], ci)? 1 : 0;
        }

        if (exp.contains(" < ")) {
            String[] parts = exp.split("<", 2);
            return interpret(parts[0], ci) < interpret(parts[1], ci)? 1 : 0;
        }

        if (exp.contains(" >= ")) {
            String[] parts = exp.split(">=", 2);
            return interpret(parts[0], ci) >= interpret(parts[1], ci)? 1 : 0;
        }

        if (exp.contains(" <= ")) {
            String[] parts = exp.split("<=", 2);
            return interpret(parts[0], ci) <= interpret(parts[1], ci)? 1 : 0;
        }

        if (exp.contains(" + ")) {
           String[] parts = exp.split("\\+", 2);
           return interpret(parts[0], ci) + interpret(parts[1], ci);
        }

        if (exp.contains(" - ")) {
            String[] parts = exp.split("-", 2);
            return interpret(parts[0], ci) - interpret(parts[1], ci);
        }

        if (exp.contains(" * ")) {
            String[] parts = exp.split("\\*", 2);
            return interpret(parts[0], ci) * interpret(parts[1], ci);
        }

        if (exp.contains(" / ")) {
            String[] parts = exp.split("/", 2);
            return interpret(parts[0], ci) / interpret(parts[1], ci);
        }

        if (exp.contains(" // ")) {
            String[] parts = exp.split("//", 2);
            return (int) (interpret(parts[0], ci) / interpret(parts[1], ci));
        }

        if (exp.contains(" % ")) {
            String[] parts = exp.split("%", 2);
            return interpret(parts[0], ci) % interpret(parts[1], ci);
        }

        try {
            return Double.parseDouble(exp);
        } catch (NumberFormatException e) {
            return Double.parseDouble(ci.vars.get(exp).toString());
        }
    }
}
