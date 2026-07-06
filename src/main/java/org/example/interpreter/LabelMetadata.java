package org.example.interpreter;

public record LabelMetadata(
        String name,
        int line,
        String[] params
) {}
