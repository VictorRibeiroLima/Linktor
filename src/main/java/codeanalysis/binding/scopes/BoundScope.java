package codeanalysis.binding.scopes;

import codeanalysis.symbol.VariableSymbol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoundScope {

    private final Map<String, VariableSymbol> variables = new HashMap<>();

    private final BoundScope parent;

    public BoundScope(BoundScope parent) {
        this.parent = parent;
    }

    public BoundScope getParent() {
        return parent;
    }

    public boolean declareVariable(VariableSymbol variable) {
        if (variables.containsKey(variable.name()))
            return false;

        variables.put(variable.name(), variable);
        return true;
    }

    public VariableSymbol getVariableByIdentifier(String identifier) {
        if (variables.containsKey(identifier)) {
            VariableSymbol variable = variables.get(identifier);
            return variable;
        }
        if (this.parent == null)
            return null;

        return parent.getVariableByIdentifier(identifier);
    }

    public VariableSymbol getLocalVariableByIdentifier(String identifier) {
        if (variables.containsKey(identifier)) {
            VariableSymbol variable = variables.get(identifier);
            return variable;
        }
        return null;
    }

    public boolean isVariablePresent(String identifier) {
        if (variables.containsKey(identifier)) {
            VariableSymbol variable = variables.get(identifier);
            return true;
        }
        if (this.parent == null)
            return false;

        return parent.isVariablePresent(identifier);
    }

    public List<VariableSymbol> getDeclaredVariables() {
        return List.copyOf(variables.values());
    }
}
