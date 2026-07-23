package org.example.interpreter.command;

import org.example.interpreter.Context;

public class ExitCommand implements Command {
    @Override
    public void execute(Context context) {
        System.exit(0);
    }
}
