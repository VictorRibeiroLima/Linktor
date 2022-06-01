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
import java.util.Scanner;

public class Linktor {
    public static void main(String[] args) {
        final String redColor = "\033[0;31m";
        final String whiteColor = "\033[0m";
        boolean showTree = false;
        StringBuilder input = new StringBuilder();
        Scanner console = new Scanner(System.in);
        Compilation previous = null;
        final Map<VariableSymbol, Object> variables = new HashMap<>();
        try {
            while (true) {
                if (input.isEmpty()) {
                    System.out.print(">");
                } else {
                    System.out.print(".");
                }
                String inLineInput = console.nextLine();
                if (inLineInput.equals("#showTree")) {
                    showTree = !showTree;
                    String isShowingTree = showTree ? "Showing parse tree" : "Not showing parse tree";
                    System.out.println(isShowingTree);
                    continue;
                } else if (inLineInput.equals("#reset")) {
                    previous = null;
                    variables.clear();
                    System.out.println("COMPILATION RESTARTED");
                    continue;
                }


                input.append(inLineInput);
                SyntaxTree tree = SyntaxTree.parse(input.toString());
                if (!inLineInput.isEmpty() && !tree.getDiagnostics().isEmpty()) {
                    input.append("\n");
                    continue;
                }
                if (showTree) {
                    tree.getRoot().writeTo(new PrintWriter(System.out, true));
                }

                Compilation compilation = previous == null ? new Compilation(tree) : previous.continueWith(tree);
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

                        System.out.println(redColor);
                        System.out.println(diagnostic);

                        String prefix = input.substring(line.getStart(), diagnostic.span().start());
                        String error = tree.getText().toString(diagnostic.span());
                        String suffix = input.substring(diagnostic.span().end());

                        System.out.print("At Line(" + lineNumber + "," + character + "): ");
                        System.out.println(whiteColor + prefix + redColor + error + whiteColor + suffix);
                    }
                }
                System.out.println(whiteColor + "-------");
                input.setLength(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("\033[0;31m" + e.getMessage());
        }
    }
}
