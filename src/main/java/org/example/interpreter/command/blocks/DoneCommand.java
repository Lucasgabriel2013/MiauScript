package org.example.interpreter.command.blocks;

import org.example.interpreter.Context;
import org.example.interpreter.command.Command;

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