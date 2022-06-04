package codeanalysis.symbol;

public class ParameterSymbol extends VariableSymbol {
    private final SymbolKind kind;

    public ParameterSymbol(String name, TypeSymbol type) {
        super(name, type, false);
        this.kind = SymbolKind.PARAMETER;
    }
}
