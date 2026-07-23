package org.example.interpreter.command;

import org.example.interpreter.Context;

public interface Command {
    void execute(Context context);
}
