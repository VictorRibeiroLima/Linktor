package codeanalysis.binding;

import codeanalysis.binding.statement.block.BoundBlockStatement;
import codeanalysis.diagnostics.DiagnosticBag;
import codeanalysis.symbol.FunctionSymbol;

import java.util.Map;

public class BoundProgram {
    private final BoundProgram previous;
    private final DiagnosticBag diagnostics;
    private final Map<FunctionSymbol, BoundBlockStatement> functionsBodies;
    private final FunctionSymbol mainFunction;

    public BoundProgram(BoundProgram previous, DiagnosticBag diagnostics, Map<FunctionSymbol, BoundBlockStatement> functionsBodies, FunctionSymbol mainFunction) {
        this.previous = previous;
        this.diagnostics = diagnostics;
        this.functionsBodies = Map.copyOf(functionsBodies);
        this.mainFunction = mainFunction;
    }

    public DiagnosticBag getDiagnostics() {
        return diagnostics;
    }

    public BoundProgram getPrevious() {
        return previous;
    }

    public FunctionSymbol getMainFunction() {
        return mainFunction;
    }

    public Map<FunctionSymbol, BoundBlockStatement> getFunctionsBodies() {
        return functionsBodies;
    }
}
