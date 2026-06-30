package org.example;

import java.util.HashMap;

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
            try {
                if (exp.contains("[") && exp.contains("]")) {
                    @SuppressWarnings("unchecked")
                    String s = ((HashMap<Integer, Object>) ci.vars.peek().get(exp.substring(0, exp.indexOf("["))))
                            .get((int) interpret(exp.substring(exp.indexOf("[") + 1, exp.indexOf("]")), ci)).toString();

                    return Double.parseDouble(s);
                }
            } catch (NumberFormatException _) {}

            return Double.parseDouble(ci.getVar(exp));
        }
    }
}
