package org.example;

import java.util.HashMap;

public class ExpressionInterpreter {
    static double interpret(String exp, CodeInterpreter ci) {
        exp = exp.trim();

        if (exp.contains(" & ")) {
            String[] parts = exp.split("&", 2);
            return (interpret(parts[0], ci) == 1) && (interpret(parts[1], ci) == 1) ? 1 : 0;
        }

        if (exp.contains(" | ")) {
            String[] parts = exp.split("\\|", 2);
            return (interpret(parts[0], ci) == 1) || (interpret(parts[1], ci) == 1) ? 1 : 0;
        }

        if (exp.contains(" == ")) {
            String[] parts = exp.split("==", 2);
            return interpret(parts[0], ci) == interpret(parts[1], ci) ? 1 : 0;
        }

        if (exp.contains(" != ")) {
            String[] parts = exp.split("!=", 2);
            return interpret(parts[0], ci) != interpret(parts[1], ci) ? 1 : 0;
        }

        if (exp.contains(" > ")) {
            String[] parts = exp.split(">", 2);
            return interpret(parts[0], ci) > interpret(parts[1], ci) ? 1 : 0;
        }

        if (exp.contains(" < ")) {
            String[] parts = exp.split("<", 2);
            return interpret(parts[0], ci) < interpret(parts[1], ci) ? 1 : 0;
        }

        if (exp.contains(" >= ")) {
            String[] parts = exp.split(">=", 2);
            return interpret(parts[0], ci) >= interpret(parts[1], ci) ? 1 : 0;
        }

        if (exp.contains(" <= ")) {
            String[] parts = exp.split("<=", 2);
            return interpret(parts[0], ci) <= interpret(parts[1], ci) ? 1 : 0;
        }

        if (exp.contains(" ^ ")) {
            String[] parts = exp.split("\\^", 2);
            return Math.pow(interpret(parts[0], ci), interpret(parts[1], ci));
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

        if (exp.contains(" + ")) {
            String[] parts = exp.split("\\+", 2);
            return interpret(parts[0], ci) + interpret(parts[1], ci);
        }

        if (exp.contains(" - ")) {
            String[] parts = exp.split("-", 2);
            return interpret(parts[0], ci) - interpret(parts[1], ci);
        }

        try {
            return Double.parseDouble(exp);
        } catch (NumberFormatException e) {
            if (exp.contains("[\"") && exp.contains("\"]")) {
                @SuppressWarnings("unchecked")
                String s = ((HashMap<Object, Object>) ci.vars.peek().get(exp.substring(0, exp.indexOf("["))))
                        .get(exp.substring(exp.indexOf("[\"") + 2, exp.indexOf("\"]"))).toString();

                return Double.parseDouble(s);
            } else if (exp.contains("[") && exp.contains("]")) {
                @SuppressWarnings("unchecked")
                String s = ((HashMap<Object, Object>) ci.vars.peek().get(exp.substring(0, exp.indexOf("["))))
                        .get(ci.vars.peek().get(exp.substring(exp.indexOf("[") + 1, exp.indexOf("]")))).toString();

                return Double.parseDouble(s);
            }

            return Double.parseDouble(ci.getVar(exp).toString());
        }
    }
}
