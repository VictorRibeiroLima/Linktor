package codeanalysis.binding;

import codeanalysis.binding.statement.block.BoundBlockStatement;
import codeanalysis.diagnostics.DiagnosticBag;
import codeanalysis.symbol.FunctionSymbol;

import java.util.Map;

public class BoundProgram {
    private final BoundBlockStatement statement;
    private final DiagnosticBag diagnostics;
    private final Map<FunctionSymbol, BoundBlockStatement> functionsBodies;

    public BoundProgram(BoundBlockStatement global, DiagnosticBag diagnostics, Map<FunctionSymbol, BoundBlockStatement> functionsBodies) {
        this.statement = global;
        this.diagnostics = diagnostics;
        this.functionsBodies = Map.copyOf(functionsBodies);
    }

    public BoundBlockStatement getStatement() {
        return statement;
    }

    public DiagnosticBag getDiagnostics() {
        return diagnostics;
    }

    public Map<FunctionSymbol, BoundBlockStatement> getFunctionsBodies() {
        return functionsBodies;
    }
}
