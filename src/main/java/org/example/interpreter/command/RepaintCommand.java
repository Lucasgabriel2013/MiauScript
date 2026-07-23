package org.example.interpreter.command;

import org.example.interpreter.Context;

public class RepaintCommand implements Command {
    @Override
    public void execute(Context context) {
        context.drawablePanel.repaint();
    }
}
