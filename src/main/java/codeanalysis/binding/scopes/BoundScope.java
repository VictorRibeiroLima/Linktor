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

    public boolean declare(VariableSymbol variable) {
        if (variables.containsKey(variable.name()))
            return false;

        variables.put(variable.name(), variable);
        return true;
    }

    public VariableSymbol lookup(VariableSymbol variableSymbol) {
        if (variables.containsKey(variableSymbol.name())) {
            VariableSymbol variable = variables.get(variableSymbol.name());
            return variable;
        }
        if (this.parent == null)
            return null;

        return parent.lookup(variableSymbol);
    }

    public List<VariableSymbol> getDeclaredVariables() {
        return List.copyOf(variables.values());
    }
}
