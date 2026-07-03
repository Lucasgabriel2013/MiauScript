package org.example;

public class ExpressionInterpreter {
    static double interpret(String exp, VariableManager variableManager) {
        exp = exp.trim();

        if (exp.contains(" & ")) {
            String[] parts = exp.split("&", 2);
            return (interpret(parts[0], variableManager) == 1) && (interpret(parts[1], variableManager) == 1) ? 1 : 0;
        }

        if (exp.contains(" | ")) {
            String[] parts = exp.split("\\|", 2);
            return (interpret(parts[0], variableManager) == 1) || (interpret(parts[1], variableManager) == 1) ? 1 : 0;
        }

        if (exp.contains(" == ")) {
            String[] parts = exp.split("==", 2);
            return interpret(parts[0], variableManager) == interpret(parts[1], variableManager) ? 1 : 0;
        }

        if (exp.contains(" != ")) {
            String[] parts = exp.split("!=", 2);
            return interpret(parts[0], variableManager) != interpret(parts[1], variableManager) ? 1 : 0;
        }

        if (exp.contains(" > ")) {
            String[] parts = exp.split(">", 2);
            return interpret(parts[0], variableManager) > interpret(parts[1], variableManager) ? 1 : 0;
        }

        if (exp.contains(" < ")) {
            String[] parts = exp.split("<", 2);
            return interpret(parts[0], variableManager) < interpret(parts[1], variableManager) ? 1 : 0;
        }

        if (exp.contains(" >= ")) {
            String[] parts = exp.split(">=", 2);
            return interpret(parts[0], variableManager) >= interpret(parts[1], variableManager) ? 1 : 0;
        }

        if (exp.contains(" <= ")) {
            String[] parts = exp.split("<=", 2);
            return interpret(parts[0], variableManager) <= interpret(parts[1], variableManager) ? 1 : 0;
        }

        if (exp.contains(" ^ ")) {
            String[] parts = exp.split("\\^", 2);
            return Math.pow(interpret(parts[0], variableManager), interpret(parts[1], variableManager));
        }

        if (exp.contains(" * ")) {
            String[] parts = exp.split("\\*", 2);
            return interpret(parts[0], variableManager) * interpret(parts[1], variableManager);
        }

        if (exp.contains(" / ")) {
            String[] parts = exp.split("/", 2);
            return interpret(parts[0], variableManager) / interpret(parts[1], variableManager);
        }

        if (exp.contains(" // ")) {
            String[] parts = exp.split("//", 2);
            return (int) (interpret(parts[0], variableManager) / interpret(parts[1], variableManager));
        }

        if (exp.contains(" % ")) {
            String[] parts = exp.split("%", 2);
            return interpret(parts[0], variableManager) % interpret(parts[1], variableManager);
        }

        if (exp.contains(" + ")) {
            String[] parts = exp.split("\\+", 2);
            return interpret(parts[0], variableManager) + interpret(parts[1], variableManager);
        }

        if (exp.contains(" - ")) {
            String[] parts = exp.split("-", 2);
            return interpret(parts[0], variableManager) - interpret(parts[1], variableManager);
        }

        try {
            return Double.parseDouble(exp);
        } catch (NumberFormatException e) {
            if (exp.contains("[\"") && exp.contains("\"]")) {
                String s = variableManager.getObject(exp.substring(0, exp.indexOf("[")))
                        .get(exp.substring(exp.indexOf("[\"") + 2, exp.indexOf("\"]"))).toString();

                return Double.parseDouble(s);
            } else if (exp.contains("[") && exp.contains("]")) {
                String s = variableManager.getObject(exp.substring(0, exp.indexOf("[")))
                        .get(variableManager.getNumber(exp.substring(exp.indexOf("[") + 1, exp.indexOf("]")))).toString();

                return Double.parseDouble(s);
            }

            return variableManager.getNumber(exp);
        }
    }
}
