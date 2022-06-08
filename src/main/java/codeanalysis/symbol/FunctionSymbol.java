package codeanalysis.symbol;

import codeanalysis.syntax.member.FunctionMemberSyntax;

import java.util.List;

public class FunctionSymbol extends Symbol {
    private final String name;
    private final TypeSymbol type;
    private final SymbolKind kind;

    private final List<ParameterSymbol> parameters;

    private final FunctionMemberSyntax declaration;

    public FunctionSymbol(String name, List<ParameterSymbol> parameters, TypeSymbol type) {
        this(name, parameters, type, null);
    }

    public FunctionSymbol(String name, List<ParameterSymbol> parameters, TypeSymbol type, FunctionMemberSyntax declaration) {
        this.name = name;
        this.type = type;
        this.parameters = List.copyOf(parameters);
        this.kind = SymbolKind.FUNCTION;
        this.declaration = declaration;
    }

    public FunctionMemberSyntax getDeclaration() {
        return declaration;
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

    @Override
    public boolean equals(Object o) {
        return o == this;
    }
}
