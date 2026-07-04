package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class VariableManager {
    private final Stack<Map<String, Object>> vars = new Stack<>();
    private final Map<String, Object> globalVars = new HashMap<>();
    private final Map<String, Object> consts = new HashMap<>();

    public void setVar(String name, Object value) {
        vars.peek().put(name, value);
    }

    public void setGlobalVar(String name, Object value) {
        globalVars.put(name, value);
    }

    public void setConst(String name, Object value) {
        if (consts.containsKey(name))
            throw new MiauScriptException("Contante repetida:", name);

        consts.put(name, value);
    }

    public void createNewFrame(HashMap<String, Object> map) {
        vars.add(map);
    }

    public void popFrame() {
        vars.pop();
    }

    public void popFrameReturning(Object objectToReturn) {
        vars.get(vars.size() - 2).put("result", objectToReturn);
        vars.pop();
    }

    public double getNumber(String name) {
        if (getVar(name) instanceof Double d) {
            return d;
        }

        throw new MiauScriptException("\"" + name + "\" deveria ser um número");
    }

    public String getString(String name) {
        if (getVar(name) instanceof String s) {
            return s;
        }

        throw new MiauScriptException("\"" + name + "\" deveria ser uma String");
    }

    public HashMap<Object, Object> getObject(String name) {
        if (getVar(name) instanceof HashMap<?,?> map) {
            @SuppressWarnings("unchecked")
            var objectMap = (HashMap<Object, Object>) map;

            return objectMap;
        }

        throw new MiauScriptException("\"" + name + "\" deveria ser um Object");
    }

    public Object getVar(String name) {
        if (consts.containsKey(name)) {
            return consts.get(name);
        }

        if (vars.peek().containsKey(name)) {
            return vars.peek().get(name);
        }

        if (globalVars.containsKey(name)) {
            return globalVars.get(name);
        }

        return null;
    }

    public boolean isDeclared(String name) {
        return getVar(name) != null;
    }
}