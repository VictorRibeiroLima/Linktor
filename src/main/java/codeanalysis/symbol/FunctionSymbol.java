package codeanalysis.symbol;

import java.util.List;

public class FunctionSymbol extends Symbol {
    private final String name;
    private final TypeSymbol type;
    private final SymbolKind kind;

    private final List<ParameterSymbol> parameters;

    public FunctionSymbol(String name, List<ParameterSymbol> parameters, TypeSymbol type) {
        this.name = name;
        this.type = type;
        this.parameters = List.copyOf(parameters);
        this.kind = SymbolKind.FUNCTION;
    }

    @Override
    public String getName() {
        return name;
    }

    public TypeSymbol getType() {
        return type;
    }

    @Override
    public SymbolKind getKind() {
        return kind;
    }

    public List<ParameterSymbol> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return name;
    }
}
