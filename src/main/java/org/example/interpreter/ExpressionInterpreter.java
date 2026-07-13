package org.example.interpreter;

public class ExpressionInterpreter {
    private final VariableManager variableManager;

    public ExpressionInterpreter(VariableManager variableManager) {
        this.variableManager = variableManager;
    }

    public Object interpret(String exp) {
        exp = exp.trim();

        if (exp.contains(" & ")) {
            String[] parts = exp.split("&", 2);
            return asDouble(interpret(parts[0])) == 1 && asDouble(interpret(parts[1])) == 1 ? 1.0 : 0.0;
        }

        if (exp.contains(" | ")) {
            String[] parts = exp.split("\\|", 2);
            return asDouble(interpret(parts[0])) == 1 || asDouble(interpret(parts[1])) == 1 ? 1.0 : 0.0;
        }

        if (exp.contains(" == ")) {
            String[] parts = exp.split("==", 2);
            return interpret(parts[0]).equals(interpret(parts[1])) ? 1.0 : 0.0;
        }

        if (exp.contains(" != ")) {
            String[] parts = exp.split("!=", 2);
            return !interpret(parts[0]).equals(interpret(parts[1])) ? 1.0 : 0.0;
        }

        if (exp.contains(" > ")) {
            String[] parts = exp.split(">", 2);
            return asDouble(interpret(parts[0])) > asDouble(interpret(parts[1])) ? 1.0 : 0.0;
        }

        if (exp.contains(" < ")) {
            String[] parts = exp.split("<", 2);
            return asDouble(interpret(parts[0])) < asDouble(interpret(parts[1])) ? 1.0 : 0.0;
        }

        if (exp.contains(" >= ")) {
            String[] parts = exp.split(">=", 2);
            return asDouble(interpret(parts[0])) >= asDouble(interpret(parts[1])) ? 1.0 : 0.0;
        }

        if (exp.contains(" <= ")) {
            String[] parts = exp.split("<=", 2);
            return asDouble(interpret(parts[0])) <= asDouble(interpret(parts[1])) ? 1.0 : 0.0;
        }

        if (exp.contains(" + ")) {
            String[] parts = exp.split("\\+", 2);

            Object firstPart = interpret(parts[0]);
            Object secondPart = interpret(parts[1]);

            if (firstPart instanceof String || secondPart instanceof String) {
                return firstPart.toString() + secondPart;
            }

            return asDouble(firstPart) + asDouble(interpret(parts[1]));
        }

        if (exp.contains(" - ")) {
            String[] parts = exp.split("-", 2);
            return asDouble(interpret(parts[0])) - asDouble(interpret(parts[1]));
        }

        if (variableManager.isDeclared(exp)) {
            return variableManager.getVar(exp);
        }

        if (exp.contains(" * ")) {
            String[] parts = exp.split("\\*", 2);
            return asDouble(interpret(parts[0])) * asDouble(interpret(parts[1]));
        }

        if (exp.contains(" / ")) {
            String[] parts = exp.split("/", 2);
            return asDouble(interpret(parts[0])) / asDouble(interpret(parts[1]));
        }

        if (exp.contains(" // ")) {
            String[] parts = exp.split("//", 2);
            return (double) (int) asDouble(interpret(parts[0])) / asDouble(interpret(parts[1]));
        }

        if (exp.contains(" % ")) {
            String[] parts = exp.split("%", 2);
            return asDouble(interpret(parts[0])) % asDouble(interpret(parts[1]));
        }

        if (exp.contains(" ^ ")) {
            String[] parts = exp.split("\\^", 2);
            return Math.pow(asDouble(interpret(parts[0])), asDouble(interpret(parts[1])));
        }

        if (exp.contains("[\"") && exp.contains("\"]")) {
            return variableManager.getObject(exp.substring(0, exp.indexOf("[")))
                    .get(exp.substring(exp.indexOf("[\"") + 2, exp.indexOf("\"]")));
        }

        if (exp.contains("[") && exp.contains("]")) {
            return variableManager.getObject(exp.substring(0, exp.indexOf("[")))
                    .get(interpret(exp.substring(exp.indexOf("[") + 1, exp.indexOf("]"))));
        }

        if (exp.startsWith("\"") && exp.endsWith("\"")) {
            return exp.substring(1, exp.length() - 1);
        }

        try {
            return Double.parseDouble(exp);
        } catch (NumberFormatException e) {
            throw new MiauScriptException("Erro na expressão:", exp);
        }
    }

    private double asDouble(Object o) {
        if (o instanceof Double num) {
            return num;
        }

        throw new MiauScriptException("\"" + o + "\" deveria ser um número");
    }
}
