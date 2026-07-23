package org.example.interpreter.command;

import org.example.interpreter.Context;

public class ClearCommand implements Command {
    @Override
    public void execute(Context context) {
        context.console.clear();
    }
}
