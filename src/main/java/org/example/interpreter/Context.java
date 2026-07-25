package org.example.interpreter;

import java.util.*;

public class Context {
    public int currentLine;

    public boolean onMainLabel = false;
    public boolean threadStopped = false;
    public final Queue<Integer> inits;
    public final Stack<Call> calls = new Stack<>();
    public VariableManager variableManager = new VariableManager();

    public final Program program;
    public final Console console;
    public final DrawablePanel drawablePanel;
    public boolean onMainThread;

    public Context(Console console, DrawablePanel drawablePanel, Program program, boolean onMainThread) {
        this.console = console;
        this.program = program;
        this.drawablePanel = drawablePanel;
        this.onMainThread = onMainThread;
        this.inits = new ArrayDeque<>(program.inits());
    }
}
