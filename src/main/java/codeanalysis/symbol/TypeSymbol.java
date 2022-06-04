package codeanalysis.symbol;

public class TypeSymbol implements ISymbol {
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
    public SymbolKind kind() {
        return SymbolKind.TYPE;
    }

    public String name() {
        return name;
    }
}
