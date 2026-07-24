package org.example.interpreter.command.screen;

import org.example.interpreter.Context;
import org.example.interpreter.command.Command;

public class RepaintCommand implements Command {
    @Override
    public void execute(Context context) {
        context.drawablePanel.repaint();
    }
}
