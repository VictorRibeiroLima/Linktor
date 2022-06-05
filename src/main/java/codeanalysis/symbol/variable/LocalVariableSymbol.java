package codeanalysis.symbol.variable;

import codeanalysis.symbol.SymbolKind;
import codeanalysis.symbol.TypeSymbol;

public class LocalVariableSymbol extends VariableSymbol {
    private final SymbolKind kind;

    public LocalVariableSymbol(String name, TypeSymbol type, boolean readOnly) {
        super(name, type, readOnly);
        this.kind = SymbolKind.LOCAL_VARIABLE;
    }

    @Override
    public SymbolKind getKind() {
        return kind;
    }
}
