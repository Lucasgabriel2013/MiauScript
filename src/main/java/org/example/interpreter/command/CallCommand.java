package org.example.interpreter.command;

import org.example.interpreter.Call;
import org.example.interpreter.Context;
import org.example.interpreter.LabelMetadata;
import org.example.interpreter.MiauScriptException;
import org.example.interpreter.expression.Expression;

import java.util.HashMap;
import java.util.List;

public class CallCommand implements Command {
    String labelName;
    List<Expression> params;
    String returnName;

    public CallCommand(String labelName, List<Expression> params, String returnName) {
        this.labelName = labelName;
        this.params = params;
        this.returnName = returnName;
    }

    @Override
    public void execute(Context context) {
        if (!context.labels.containsKey(labelName)) {
            throw new MiauScriptException("Label " + labelName + " não existe: ");
        }

        LabelMetadata label = context.labels.get(labelName);

        HashMap<String, Object> newFrame = new HashMap<>();

        Call call;

        if (label.params() != null) {
            for (int i = 0; i < label.params().length; i++) {
                Object exp = params.get(i).evaluate(context.variableManager);
                newFrame.put(label.params()[i], exp);
            }
        }

        if (!returnName.isEmpty()) {
            call = new Call(context.currentLine, returnName);
        } else {
            call = new Call(context.currentLine, "result");
        }

        context.variableManager.createNewFrame(newFrame);
        context.calls.add(call);
        context.currentLine = context.labels.get(labelName).line();
    }
}