import codeanalysis.binding.Binder;
import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.diagnostics.Diagnostic;
import codeanalysis.diagnostics.DiagnosticBag;
import codeanalysis.diagnostics.text.SourceText;
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
        Scanner console = new Scanner(System.in);
        final Map<VariableSymbol, Object> variables = new HashMap<>();
        try {
            while (true) {
                String input = console.nextLine();
                if (input.equals("#showTree")) {
                    showTree = true;
                    console.nextLine();
                    continue;
                }
                SyntaxTree tree = SyntaxTree.parse(input);
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
                        int lineNumber = lineIndex + 1;
                        int character = diagnostic.span().start() - text.getLines().get(lineIndex).getStart() + 1;

                        System.out.println(redColor);
                        System.out.println(diagnostic);

                        String prefix = input.substring(0, diagnostic.span().start());
                        String error = input.substring(diagnostic.span().start(), diagnostic.span().end());
                        String suffix = input.substring(diagnostic.span().end());

                        System.out.print("At Line(" + lineNumber + "," + character + "): ");
                        System.out.println(whiteColor + prefix + redColor + error + whiteColor + suffix);
                    }
                }
                System.out.println(whiteColor + "-------");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("\033[0;31m" + e.getMessage());
        }
    }
}
