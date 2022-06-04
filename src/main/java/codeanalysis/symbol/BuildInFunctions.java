package codeanalysis.symbol;

import java.util.List;

public class BuildInFunctions {
    public static final FunctionSymbol PRINT = new FunctionSymbol("print", List.of(new ParameterSymbol("text", TypeSymbol.STRING)), TypeSymbol.VOID);
    public static final FunctionSymbol PRINTF = new FunctionSymbol("printf", List.of(new ParameterSymbol("text", TypeSymbol.STRING)), TypeSymbol.VOID);
    public static final FunctionSymbol READ = new FunctionSymbol("read", List.of(), TypeSymbol.STRING);
    public static final FunctionSymbol RANDOM = new FunctionSymbol("random", List.of(new ParameterSymbol("max", TypeSymbol.INTEGER)), TypeSymbol.INTEGER);

    private BuildInFunctions() {
    }

    public static List<FunctionSymbol> getAll() {
        return List.of(PRINT, PRINTF, READ, RANDOM);
    }
}
