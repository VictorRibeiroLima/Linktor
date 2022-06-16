package repl;

import codeanalysis.diagnostics.Diagnostic;
import codeanalysis.symbol.variable.VariableSymbol;
import codeanalysis.syntax.SyntaxTree;
import compilation.Compilation;
import compilation.EvaluationResult;
import io.DiagnosticsWriter;
import util.ConsoleColors;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinktorRepl extends Repl {
    private boolean showTree;
    private boolean showProgram;
    private Compilation previous;
    private final Map<VariableSymbol, Object> variables = new HashMap<>();

    protected void evaluate(String input) throws Exception {
        SyntaxTree tree = SyntaxTree.parse(input);


        Compilation compilation = Compilation.createScript(previous, tree);
        if (showTree) {
            tree.getRoot().writeTo(new PrintWriter(System.out, true));
            System.out.println();
        }
        if (showProgram) {
            compilation.emitTree(new PrintWriter(System.out, true));
            System.out.println();
        }
        EvaluationResult evaluationResult = compilation.evaluate(variables);
        List<Diagnostic> diagnostics = evaluationResult.diagnostics();
        Object result = evaluationResult.result();
        if (diagnostics.isEmpty()) {
            if (result != null)
                System.out.println(ConsoleColors.YELLOW_BRIGHT + "Result: " + result);
            previous = compilation;
        } else {
            DiagnosticsWriter.write(diagnostics);
        }
    }

    protected boolean isCompleteSubmission(String text, StringBuilder input) {
        if (text == null || text.isEmpty())
            return false;

        SyntaxTree tree = SyntaxTree.parse(input.toString());
        return tree.getRoot().getMembers().get(tree.getRoot().getMembers().size() - 1).getLastToken().isMissing();
    }

    protected boolean evaluateMetaCommand(String inLineInput) {
        switch (inLineInput) {
            case "#showTree" -> {
                showTree = !showTree;
                String isShowingTree = showTree ? "Showing parse tree" : "Not showing parse tree";
                System.out.println(isShowingTree);
                return true;
            }
            case "#reset" -> {
                previous = null;
                variables.clear();
                System.out.println("COMPILATION RESTARTED");
                return true;
            }
            case "#showProgram" -> {
                showProgram = !showProgram;
                String isShowingTree = showProgram ? "Showing program tree" : "Not showing program tree";
                System.out.println(isShowingTree);
                return true;
            }
        }
        return false;
    }
}
