package org.example;

public record LabelMetadata(
        String name,
        int line,
        String[] params
) {}
