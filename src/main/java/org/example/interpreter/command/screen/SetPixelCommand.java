package org.example.interpreter.command.screen;

import org.example.interpreter.Context;
import org.example.interpreter.MiauScriptException;
import org.example.interpreter.command.Command;
import org.example.interpreter.expression.Expression;

import java.awt.*;

public class SetPixelCommand implements Command {
    Expression xExp;
    Expression yExp;
    Expression rExp;
    Expression gExp;
    Expression bExp;

    public SetPixelCommand(Expression xExp, Expression yExp, Expression rExp, Expression gExp, Expression bExp) {
        this.xExp = xExp;
        this.yExp = yExp;
        this.rExp = rExp;
        this.gExp = gExp;
        this.bExp = bExp;
    }

    @Override
    public void execute(Context context) {
        Object firstPart = xExp.evaluate(context.variableManager);
        Object secondPart = yExp.evaluate(context.variableManager);
        Object thirdPart = rExp.evaluate(context.variableManager);
        Object fourthPart = gExp.evaluate(context.variableManager);
        Object fifthPart = bExp.evaluate(context.variableManager);

        if (firstPart instanceof Double x
                && secondPart instanceof Double y
                && thirdPart instanceof Double r
                && fourthPart instanceof Double g
                && fifthPart instanceof Double b) {

            if (isColorValid(r.intValue()) && isColorValid(g.intValue()) && isColorValid(b.intValue())
                && x.intValue() >= 0 && x.intValue() < 100 && y.intValue() >= 0 && y.intValue() < 50 ) {

                context.drawablePanel.setPixelColor(x.intValue(), y.intValue(), new Color(r.intValue(), g.intValue(), b.intValue()));
                return;
            }

            throw new MiauScriptException("Erro no setPixel por IndexOutOfBounds");
        }

        throw new MiauScriptException("Erro no setPixel por tipo inválido");
    }

    private boolean isColorValid(int color) {
        return color >= 0 && color < 256;
    }
}
