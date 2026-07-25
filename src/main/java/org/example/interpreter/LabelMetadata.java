package org.example.interpreter;

import java.util.List;

public record LabelMetadata(
        String name,
        int line,
        List<String> params
) {}
