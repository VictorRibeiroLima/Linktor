package codeanalysis.symbol;

public class TypeSymbol extends Symbol {
    public static final TypeSymbol VOID = new TypeSymbol("void");
    private final String name;
    public static final TypeSymbol ERROR = new TypeSymbol("?");
    public static final TypeSymbol BOOLEAN = new TypeSymbol("boolean");
    public static final TypeSymbol INTEGER = new TypeSymbol("int");
    public static final TypeSymbol STRING = new TypeSymbol("string");

    private TypeSymbol(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return name;
    }

    @Override
    public SymbolKind getKind() {
        return SymbolKind.TYPE;
    }

    public String getName() {
        return name;
    }
}
