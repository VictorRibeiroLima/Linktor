package codeanalysis.binding.scopes;

import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.diagnostics.Diagnostic;
import codeanalysis.symbol.VariableSymbol;

import java.util.Collections;
import java.util.List;

public class BoundGlobalScope {
    private final BoundGlobalScope previous;

    private final List<Diagnostic> diagnostics;

    private final List<VariableSymbol> variables;

    private final BoundExpression expression;

    public BoundGlobalScope(BoundGlobalScope previous, List<Diagnostic> diagnostics, List<VariableSymbol> variables, BoundExpression expression) {
        this.previous = previous;
        this.diagnostics = Collections.unmodifiableList(diagnostics);
        this.variables = Collections.unmodifiableList(variables);
        this.expression = expression;
    }

    public BoundGlobalScope getPrevious() {
        return previous;
    }

    public List<Diagnostic> getDiagnostics() {
        return diagnostics;
    }

    public List<VariableSymbol> getVariables() {
        return variables;
    }

    public BoundExpression getExpression() {
        return expression;
    }
}
