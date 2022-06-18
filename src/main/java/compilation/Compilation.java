package compilation;

import codeanalysis.binding.Binder;
import codeanalysis.binding.BoundProgram;
import codeanalysis.binding.scopes.BoundGlobalScope;
import codeanalysis.controlflow.ControlFlowGraph;
import codeanalysis.diagnostics.Diagnostic;
import codeanalysis.evaluator.Evaluator;
import codeanalysis.symbol.variable.VariableSymbol;
import codeanalysis.syntax.SyntaxTree;
import emit.Emitter;
import io.BoundNodeWriter;
import io.DiagnosticsWriter;
import io.SymbolWriter;

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

    private Compilation(Compilation previous, SyntaxTree... trees) {
        this.trees = List.of(trees);
        this.previous = previous;
    }

    public static Compilation create(SyntaxTree... trees) {
        return new Compilation(null, trees);
    }

    public static Compilation createScript(Compilation previous, SyntaxTree... trees) {
        return new Compilation(previous, trees);
    }

    public List<SyntaxTree> getTrees() {
        return trees;
    }

    public Compilation getPrevious() {
        return previous;
    }

    private BoundProgram getProgram() throws Exception {
        var previous = getPrevious() == null ? null : getPrevious().getProgram();
        return Binder.bindProgram(previous, getGlobalScope());
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
        trees.forEach(tree -> treesDiagnostics.addAll(tree.getDiagnostics()));
        List<Diagnostic> diagnostics = Stream.concat(globalScope.getDiagnostics().stream(),
                        treesDiagnostics.stream())
                .collect(Collectors.toList());

        if (!diagnostics.isEmpty())
            return new EvaluationResult(diagnostics, null);

        BoundProgram program = getProgram();

        if (!program.getDiagnostics().isEmpty())
            return new EvaluationResult(program.getDiagnostics().getDiagnostics(), null);
        Evaluator evaluator = new Evaluator(program, variables);
        Object result = evaluator.evaluate();
        return new EvaluationResult(diagnostics, result);
    }

    public void emmit() throws Exception {
        BoundGlobalScope globalScope = getGlobalScope();
        var treesDiagnostics = new ArrayList<Diagnostic>();
        trees.forEach(tree -> treesDiagnostics.addAll(tree.getDiagnostics()));
        List<Diagnostic> diagnostics = Stream.concat(globalScope.getDiagnostics().stream(),
                        treesDiagnostics.stream())
                .collect(Collectors.toList());
        if (!diagnostics.isEmpty()) {
            DiagnosticsWriter.write(diagnostics);
            return;
        }
        var program = getProgram();
        if (!program.getDiagnostics().isEmpty()) {
            DiagnosticsWriter.write(program.getDiagnostics().toUnmodifiableList());
            return;
        }
        var emitter = new Emitter(program);
        emitter.emit();
    }

    public void emitTree(PrintWriter printWriter) throws Exception {
        BoundProgram program = getProgram();
        for (var function : program.getFunctionsBodies().entrySet()) {
            SymbolWriter.writeTo(printWriter, function.getKey());
            printWriter.println();
            BoundNodeWriter.writeTo(printWriter, function.getValue());
            printWriter.println();
            printWriter.println();
        }

    }

    public void writeFlowGraph() throws Exception {
        BoundProgram program = getProgram();
        var fileName = trees.get(0).getRoot().getLocation().fileName();
        for (var function : program.getFunctionsBodies().entrySet()) {
            var cfgStatements = function.getValue();

            var cfg = ControlFlowGraph.create(cfgStatements);


            var root = new File(fileName.substring(0, fileName.lastIndexOf('/'))).getCanonicalPath();
            var file = new File(root + "/" + function.getKey().getName() + ".dot");
            cfg.writeTo(new PrintWriter(file));
        }
    }
}
