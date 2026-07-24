package org.example.interpreter.command.out;

import org.example.interpreter.Context;
import org.example.interpreter.command.Command;

public class ClearCommand implements Command {
    @Override
    public void execute(Context context) {
        context.console.clear();
    }
}
