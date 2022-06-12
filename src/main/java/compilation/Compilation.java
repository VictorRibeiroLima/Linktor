package compilation;

import codeanalysis.binding.Binder;
import codeanalysis.binding.BoundProgram;
import codeanalysis.binding.scopes.BoundGlobalScope;
import codeanalysis.binding.statement.block.BoundBlockStatement;
import codeanalysis.controlflow.ControlFlowGraph;
import codeanalysis.diagnostics.Diagnostic;
import codeanalysis.evaluator.Evaluator;
import codeanalysis.symbol.variable.VariableSymbol;
import codeanalysis.syntax.SyntaxTree;
import io.BoundNodeWriter;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Compilation {
    private final Compilation previous;
    private final List<SyntaxTree> trees;

    private final AtomicReference<BoundGlobalScope> globalScope = new AtomicReference<>();

    public Compilation(SyntaxTree... trees) {
        this(null, trees);
    }

    private Compilation(Compilation previous, SyntaxTree... trees) {
        this.trees = List.of(trees);
        this.previous = previous;
    }

    public List<SyntaxTree> getTrees() {
        return trees;
    }

    public Compilation getPrevious() {
        return previous;
    }

    private BoundGlobalScope getGlobalScope() throws Exception {
        if (this.globalScope.get() == null) {
            BoundGlobalScope previousScope = this.previous == null ? null : previous.getGlobalScope();
            BoundGlobalScope globalScope = Binder.bindGlobalScope(trees, previousScope);
            this.globalScope.compareAndSet(null, globalScope);
        }
        return this.globalScope.get();
    }

    public Compilation continueWith(SyntaxTree... trees) {
        return new Compilation(this, trees);
    }

    public EvaluationResult evaluate(Map<VariableSymbol, Object> variables) throws Exception {
        BoundGlobalScope globalScope = getGlobalScope();
        var treesDiagnostics = new ArrayList<Diagnostic>();
        trees.forEach(tree -> {
            treesDiagnostics.addAll(tree.getDiagnostics());
        });
        List<Diagnostic> diagnostics = Stream.concat(globalScope.getDiagnostics().stream(),
                        treesDiagnostics.stream())
                .collect(Collectors.toList());

        if (!diagnostics.isEmpty())
            return new EvaluationResult(diagnostics, null);

        BoundProgram program = Binder.bindProgram(getGlobalScope());
        var cfgStatements = program.getStatement().getStatements().isEmpty() && !program.getFunctionsBodies().isEmpty()
                ? (BoundBlockStatement) program.getFunctionsBodies().values().toArray()[program.getFunctionsBodies().size() - 1]
                : program.getStatement();

        var cfg = ControlFlowGraph.create(cfgStatements);

        var root = new File(".").getCanonicalPath();
        var file = new File(root + "/cfg.dot");
        cfg.writeTo(new PrintWriter(file));

        if (!program.getDiagnostics().isEmpty())
            return new EvaluationResult(program.getDiagnostics().getDiagnostics(), null);
        Evaluator evaluator = new Evaluator(program, variables);
        Object result = evaluator.evaluate();
        return new EvaluationResult(diagnostics, result);
    }

    public void emitTree(PrintWriter printWriter) throws Exception {
        BoundProgram program = Binder.bindProgram(getGlobalScope());
        var node = program.getStatement();
        BoundNodeWriter.writeTo(printWriter, node);
    }
}
