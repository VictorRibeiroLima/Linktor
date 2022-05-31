package compilation;

import codeanalysis.binding.Binder;
import codeanalysis.binding.scopes.BoundGlobalScope;
import codeanalysis.diagnostics.Diagnostic;
import codeanalysis.evaluator.Evaluator;
import codeanalysis.symbol.VariableSymbol;
import codeanalysis.syntax.SyntaxTree;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Compilation {
    private final Compilation previous;
    private final SyntaxTree tree;

    private final AtomicReference<BoundGlobalScope> globalScope = new AtomicReference<>();

    public Compilation(SyntaxTree tree) {
        this(null, tree);
    }

    private Compilation(Compilation previous, SyntaxTree tree) {
        this.tree = tree;
        this.previous = previous;
    }

    public SyntaxTree getTree() {
        return tree;
    }

    public Compilation getPrevious() {
        return previous;
    }

    private BoundGlobalScope getGlobalScope() throws Exception {
        if (this.globalScope.get() == null) {
            BoundGlobalScope previousScope = this.previous == null ? null : previous.getGlobalScope();
            BoundGlobalScope globalScope = Binder.boundGlobalScope(this.tree.getRoot(), previousScope);
            this.globalScope.compareAndSet(null, globalScope);
        }
        return this.globalScope.get();
    }

    public Compilation continueWith(SyntaxTree tree) {
        return new Compilation(this, tree);
    }

    public EvaluationResult evaluate(Map<VariableSymbol, Object> variables) throws Exception {
        BoundGlobalScope globalScope = getGlobalScope();
        List<Diagnostic> diagnostics = Stream.concat(globalScope.getDiagnostics().stream(),
                        tree.getDiagnostics().getDiagnostics().stream())
                .collect(Collectors.toList());

        if (!diagnostics.isEmpty())
            return new EvaluationResult(diagnostics, null);

        Evaluator evaluator = new Evaluator(globalScope.getStatement(), variables);
        Object result = evaluator.evaluate();
        return new EvaluationResult(diagnostics, result);
    }
}
