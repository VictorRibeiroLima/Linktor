package codeanalysis.binding.scopes;

import codeanalysis.binding.scopes.identifier.FunctionIdentifier;
import codeanalysis.symbol.FunctionSymbol;
import codeanalysis.symbol.ParameterSymbol;
import codeanalysis.symbol.TypeSymbol;
import codeanalysis.symbol.variable.VariableSymbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoundScope {

    private Map<String, VariableSymbol> variables;
    private Map<FunctionIdentifier, FunctionSymbol> functions;

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
        FunctionIdentifier identifier = new FunctionIdentifier(function.getName(), getFunctionParamTypes(function));
        if (functions.containsKey(identifier))
            return false;

        functions.put(identifier, function);
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

    public FunctionSymbol getFunctionsByIdentifier(FunctionIdentifier identifier) {
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

    public FunctionSymbol getLocalFunctionByIdentifier(FunctionIdentifier identifier) {
        if (functions == null)
            return null;
        if (functions.containsKey(identifier)) {
            return functions.get(identifier);
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

    public boolean isFunctionPresent(FunctionIdentifier identifier) {
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

    private List<TypeSymbol> getFunctionParamTypes(FunctionSymbol function) {
        List<TypeSymbol> types = new ArrayList<>();
        for (ParameterSymbol param : function.getParameters()) {
            types.add(param.getType());
        }
        return List.copyOf(types);
    }
}
