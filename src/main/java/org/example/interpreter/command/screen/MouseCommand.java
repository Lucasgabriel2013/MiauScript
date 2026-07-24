package org.example.interpreter.command.screen;

import org.example.interpreter.Context;
import org.example.interpreter.MiauScriptException;
import org.example.interpreter.command.Command;
import org.example.interpreter.expression.Expression;

public class MouseCommand implements Command {
    Expression x, y;
    String varName;

    public MouseCommand(Expression x, Expression y, String varName) {
        this.x = x;
        this.y = y;
        this.varName = varName;
    }
    
    @Override
    public void execute(Context context) {
        Object firstPart = x.evaluate(context.variableManager);
        Object secondPart = y.evaluate(context.variableManager);

        if (firstPart instanceof Double dx && secondPart instanceof Double dy) {
            context.variableManager.setVar(varName, context.drawablePanel.pixelIsClicked(dx.intValue(), dy.intValue())? 1.0 : 0.0);
            return;
        }

        throw new MiauScriptException("Erro no mouse", firstPart + " e " + secondPart + " deveriam ser números");
    }
}