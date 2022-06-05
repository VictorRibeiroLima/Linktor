package codeanalysis.symbol.variable;

import codeanalysis.symbol.Symbol;
import codeanalysis.symbol.TypeSymbol;

public abstract class VariableSymbol extends Symbol {
    private final String name;

    private final TypeSymbol type;

    private final boolean readOnly;

    public VariableSymbol(String name, TypeSymbol type, boolean readOnly) {
        this.name = name;
        this.type = type;
        this.readOnly = readOnly;

    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }


    @Override
    public String getName() {
        return name;
    }


    public boolean isReadOnly() {
        return readOnly;
    }

    public TypeSymbol getType() {
        return type;
    }

}
