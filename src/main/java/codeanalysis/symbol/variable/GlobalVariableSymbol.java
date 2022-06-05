package codeanalysis.symbol.variable;

import codeanalysis.symbol.SymbolKind;
import codeanalysis.symbol.TypeSymbol;

public class GlobalVariableSymbol extends VariableSymbol {
    private final SymbolKind kind;

    public GlobalVariableSymbol(String name, TypeSymbol type, boolean readOnly) {
        super(name, type, readOnly);
        this.kind = SymbolKind.GLOBAL_VARIABLE;
    }

    @Override
    public SymbolKind getKind() {
        return kind;
    }
}
