package org.example.interpreter;

import org.example.interpreter.command.Command;

import java.util.List;
import java.util.Map;

public record Program(
        List<Command> commands,
        Map<String, LabelMetadata> labels,
        List<Integer> inits
) {
}
