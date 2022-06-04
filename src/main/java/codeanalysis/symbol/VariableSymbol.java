package codeanalysis.symbol;

public record VariableSymbol(String name, TypeSymbol type, boolean readOnly) implements ISymbol {

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    public SymbolKind kind() {
        return SymbolKind.VARIABLE;
    }

}
