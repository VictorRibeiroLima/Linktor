package codeanalysis.binding.scopes;

import codeanalysis.binding.statement.BoundStatement;
import codeanalysis.diagnostics.Diagnostic;
import codeanalysis.symbol.FunctionSymbol;
import codeanalysis.symbol.variable.VariableSymbol;

import java.util.List;

public class BoundGlobalScope {
    private final BoundGlobalScope previous;

    private final List<Diagnostic> diagnostics;

    private final List<VariableSymbol> variables;
    private final List<FunctionSymbol> functions;

    private final List<BoundStatement> statements;

    public BoundGlobalScope(BoundGlobalScope previous, List<Diagnostic> diagnostics, List<VariableSymbol> variables, List<FunctionSymbol> functions, List<BoundStatement> statement) {
        this.previous = previous;
        this.diagnostics = List.copyOf(diagnostics);
        this.variables = List.copyOf(variables);
        this.functions = List.copyOf(functions);
        this.statements = List.copyOf(statement);
    }

    public BoundGlobalScope getPrevious() {
        return previous;
    }

    public List<Diagnostic> getDiagnostics() {
        return diagnostics;
    }

    public List<FunctionSymbol> getFunctions() {
        return functions;
    }

    public List<VariableSymbol> getVariables() {
        return variables;
    }

    public List<BoundStatement> getStatements() {
        return statements;
    }
}
