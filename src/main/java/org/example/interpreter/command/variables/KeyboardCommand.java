package org.example.interpreter.command.variables;

import org.example.interpreter.Context;
import org.example.interpreter.MiauScriptException;
import org.example.interpreter.command.Command;
import org.example.interpreter.expression.Expression;
import org.example.screen.Key;

public class KeyboardCommand implements Command {
    Expression expression;

    public KeyboardCommand(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void execute(Context context) {
        String key = expression.evaluate(context.variableManager).toString();

        try {
            context.variableManager.setVar(key, context.console.isPressed(Key.valueOf(key.toUpperCase())) ? 1.0 : 0.0);
        } catch (IllegalArgumentException e) {
            throw new MiauScriptException("Keyboard contém uma tecla invalída");
        }
    }
}
