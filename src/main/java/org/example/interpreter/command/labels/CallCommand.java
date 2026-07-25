package org.example.interpreter.command.labels;

import org.example.interpreter.Call;
import org.example.interpreter.Context;
import org.example.interpreter.LabelMetadata;
import org.example.interpreter.MiauScriptException;
import org.example.interpreter.command.Command;
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
        if (!context.program.labels().containsKey(labelName)) {
            throw new MiauScriptException("Label " + labelName + " não existe");
        }

        LabelMetadata label = context.program.labels().get(labelName);

        HashMap<String, Object> newFrame = new HashMap<>();

        Call call;

        if (label.params() != null) {
            for (int i = 0; i < label.params().size(); i++) {
                Object exp = params.get(i).evaluate(context.variableManager);
                newFrame.put(label.params().get(i), exp);
            }
        }

        if (!returnName.isEmpty()) {
            call = new Call(context.currentLine, returnName);
        } else {
            call = new Call(context.currentLine, "result");
        }

        context.variableManager.createNewFrame(newFrame);
        context.calls.add(call);
        context.currentLine = context.program.labels().get(labelName).line();
    }
}