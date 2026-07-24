package org.example.interpreter.command.labels;

import org.example.interpreter.Context;
import org.example.interpreter.command.Command;
import org.example.interpreter.expression.Expression;

import java.util.HashMap;

public class ReturnCommand implements Command {
    Expression toReturn;

    public ReturnCommand(Expression toReturn) {
        this.toReturn = toReturn;
    }

    @Override
    public void execute(Context context) {
        if (toReturn != null) {
            context.variableManager.popFrameReturning(toReturn.evaluate(context.variableManager), context.calls.peek().returnName());
        } else if (!context.calls.isEmpty()) {
            context.variableManager.popFrameReturning(0.0, context.calls.peek().returnName());
        } else {
            context.variableManager.popFrame();
        }

        if (context.calls.isEmpty() && context.inits.isEmpty()) {
            context.currentLine = context.labels.get("main").line();
            context.variableManager.createNewFrame(new HashMap<>());
            return;
        }

        if (context.calls.isEmpty()) {
            context.currentLine = context.inits.remove();
            context.variableManager.createNewFrame(new HashMap<>());
            return;
        }

        context.currentLine = context.calls.pop().line();
    }
}