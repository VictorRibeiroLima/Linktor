import codeanalysis.binding.Binder;
import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.diagnostics.Diagnostic;
import codeanalysis.diagnostics.DiagnosticBag;
import codeanalysis.diagnostics.text.SourceText;
import codeanalysis.diagnostics.text.TextLine;
import codeanalysis.evaluator.Evaluator;
import codeanalysis.symbol.VariableSymbol;
import codeanalysis.syntax.SyntaxTree;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Linktor {
    public static void main(String[] args) {
        final String redColor = "\033[0;31m";
        final String whiteColor = "\033[0m";
        boolean showTree = false;
        StringBuilder input = new StringBuilder();
        Scanner console = new Scanner(System.in);
        final Map<VariableSymbol, Object> variables = new HashMap<>();
        try {
            while (true) {
                if (input.isEmpty()) {
                    System.out.print(">");
                } else {
                    System.out.print("...");
                }
                String inLineInput = console.nextLine();
                if (inLineInput.equals("#showTree")) {
                    showTree = !showTree;
                    String isShowingTree = showTree ? "Showing parse tree" : "Not showing parse tree";
                    System.out.println(isShowingTree);
                    console.nextLine();
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

                Binder binder = new Binder(variables);

                BoundExpression bound = binder.bindExpression(tree.getRoot());
                DiagnosticBag diagnostics = binder.getDiagnostics().concat(tree.getDiagnostics());
                if (diagnostics.isEmpty()) {
                    Evaluator evaluator = new Evaluator(bound, variables);
                    Object result = evaluator.evaluate();
                    System.out.println("Result: " + result);
                } else {
                    SourceText text = tree.getText();
                    for (Diagnostic diagnostic : diagnostics) {
                        int lineIndex = text.getLineIndex(diagnostic.span().start());
                        TextLine line = text.getLines().get(lineIndex);
                        int lineNumber = lineIndex + 1;
                        int character = diagnostic.span().start() - line.getStart() + 1;

                        System.out.println(redColor);
                        System.out.println(diagnostic);

                        String error = tree.getText().toString(diagnostic.span());

                        System.out.print("At Line(" + lineNumber + "," + character + "): ");
                        System.out.println(redColor + error);
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
