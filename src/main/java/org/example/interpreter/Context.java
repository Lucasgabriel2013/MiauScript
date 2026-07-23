package org.example.interpreter;

import java.util.*;

public class Context {
    public int currentLine;

    public final Queue<Integer> inits = new ArrayDeque<>();
    public final Stack<Call> calls = new Stack<>();
    public final Map<String, LabelMetadata> labels = new HashMap<>();
    public final VariableManager variableManager = new VariableManager();

    public final Console console;
    public final DrawablePanel drawablePanel;

    public Context(Console console, DrawablePanel drawablePanel) {
        this.console = console;
        this.drawablePanel = drawablePanel;
    }
}
