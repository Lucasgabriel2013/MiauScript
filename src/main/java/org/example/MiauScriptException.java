package org.example;

public class MiauScriptException extends RuntimeException {
    private String line = "";

    public MiauScriptException(String message, String line) {
        super(message);
        this.line = line;
    }

    public MiauScriptException(String message) {
        super(message);
    }

    public String getLine() {
        return line;
    }
}
