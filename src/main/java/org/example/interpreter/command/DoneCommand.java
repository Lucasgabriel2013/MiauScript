package org.example.interpreter.command;

import org.example.interpreter.Context;

public class DoneCommand implements Command {
    int whileLine;

    public DoneCommand(int whileLine) {
        this.whileLine = whileLine;
    }

    @Override
    public void execute(Context context) {
        context.currentLine = whileLine - 1;
    }
}