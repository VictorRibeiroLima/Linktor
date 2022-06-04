package repl;

import codeanalysis.diagnostics.Diagnostic;
import codeanalysis.diagnostics.text.SourceText;
import codeanalysis.diagnostics.text.TextLine;
import codeanalysis.symbol.VariableSymbol;
import codeanalysis.syntax.SyntaxTree;
import compilation.Compilation;
import compilation.EvaluationResult;
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


        Compilation compilation = previous == null ? new Compilation(tree) : previous.continueWith(tree);
        if (showTree) {
            tree.getRoot().writeTo(new PrintWriter(System.out, true));
        }
        if (showProgram) {
            compilation.emitTree(new PrintWriter(System.out, true));
        }
        EvaluationResult evaluationResult = compilation.evaluate(variables);
        List<Diagnostic> diagnostics = evaluationResult.diagnostics();
        Object result = evaluationResult.result();
        if (diagnostics.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW_BRIGHT + "Result: " + result);
            previous = compilation;
        } else {
            SourceText text = tree.getText();
            for (Diagnostic diagnostic : diagnostics) {
                int lineIndex = text.getLineIndex(diagnostic.span().start());
                TextLine line = text.getLines().get(lineIndex);
                int lineNumber = lineIndex + 1;
                int character = diagnostic.span().start() - line.getStart() + 1;

                System.out.println(ConsoleColors.RED);
                System.out.println(diagnostic);

                String prefix = input.substring(line.getStart(), diagnostic.span().start());
                String error = tree.getText().toString(diagnostic.span());
                String suffix = input.substring(diagnostic.span().end());

                System.out.print("At Line(" + lineNumber + "," + character + "): ");
                System.out.println(ConsoleColors.WHITE + prefix + ConsoleColors.RED + error + ConsoleColors.WHITE + suffix);
            }
        }
    }

    protected boolean isCompleteSubmission(String text) {
        if (text == null || text.isEmpty())
            return false;

        SyntaxTree tree = SyntaxTree.parse(text);
        return tree.getRoot().getStatement().getLastToken().isMissing();
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
