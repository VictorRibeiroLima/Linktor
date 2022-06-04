package codeanalysis.binding.scopes;

import codeanalysis.symbol.FunctionSymbol;
import codeanalysis.symbol.VariableSymbol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoundScope {

    private Map<String, VariableSymbol> variables;
    private Map<String, FunctionSymbol> functions;

    private final BoundScope parent;

    public BoundScope(BoundScope parent) {
        this.parent = parent;
    }

    public BoundScope getParent() {
        return parent;
    }

    public boolean declareVariable(VariableSymbol variable) {
        if (variables == null)
            variables = new HashMap<>();
        if (variables.containsKey(variable.getName()))
            return false;

        variables.put(variable.getName(), variable);
        return true;
    }

    public boolean declareFunction(FunctionSymbol function) {
        if (functions == null)
            functions = new HashMap<>();
        if (functions.containsKey(function.getName()))
            return false;

        functions.put(function.getName(), function);
        return true;
    }

    public VariableSymbol getVariableByIdentifier(String identifier) {
        if (variables != null && variables.containsKey(identifier)) {
            return variables.get(identifier);
        }
        if (this.parent == null)
            return null;

        return parent.getVariableByIdentifier(identifier);
    }

    public FunctionSymbol getFunctionsByIdentifier(String identifier) {
        if (functions != null && functions.containsKey(identifier)) {
            return functions.get(identifier);
        }
        if (this.parent == null)
            return null;

        return parent.getFunctionsByIdentifier(identifier);
    }

    public VariableSymbol getLocalVariableByIdentifier(String identifier) {
        if (variables == null)
            return null;
        if (variables.containsKey(identifier)) {
            return variables.get(identifier);
        }
        return null;
    }

    public FunctionSymbol getLocalFunctionByIdentifier(String identifier) {
        if (functions == null)
            return null;
        if (functions.containsKey(identifier)) {
            FunctionSymbol variable = functions.get(identifier);
            return variable;
        }
        return null;
    }

    public boolean isVariablePresent(String identifier) {
        if (variables != null && variables.containsKey(identifier)) {
            return true;
        }
        if (this.parent == null)
            return false;

        return parent.isVariablePresent(identifier);
    }

    public boolean isFunctionPresent(String identifier) {
        if (functions != null && functions.containsKey(identifier)) {
            return true;
        }
        if (this.parent == null)
            return false;

        return parent.isFunctionPresent(identifier);
    }

    public List<VariableSymbol> getDeclaredVariables() {
        if (variables == null)
            return List.of();
        return List.copyOf(variables.values());
    }

    public List<FunctionSymbol> getDeclaredFunctions() {
        if (functions == null)
            return List.of();
        return List.copyOf(functions.values());
    }
}
