import codeanalysis.binding.Binder;
import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.diagnostics.Diagnostic;
import codeanalysis.diagnostics.DiagnosticBag;
import codeanalysis.evaluator.Evaluator;
import codeanalysis.symbol.VariableSymbol;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;
import codeanalysis.syntax.SyntaxTree;

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
                    printTree(tree.getRoot());
                }

                Binder binder = new Binder(variables);

                BoundExpression bound = binder.bindExpression(tree.getRoot());
                DiagnosticBag diagnostics = binder.getDiagnostics().concat(tree.getDiagnostics());
                if (!diagnostics.isEmpty()) {
                    for (Diagnostic diagnostic : diagnostics) {
                        System.out.println(redColor);
                        System.out.println(diagnostic);

                        String prefix = input.substring(0, diagnostic.span().start());
                        String error = input.substring(diagnostic.span().start(), diagnostic.span().end());
                        String suffix = input.substring(diagnostic.span().end());

                        System.out.println(whiteColor + prefix + redColor + error + whiteColor + suffix);

                    }
                } else {
                    Evaluator evaluator = new Evaluator(bound, variables);
                    Object result = evaluator.evaluate();
                    System.out.println("Result: " + result);

                }
                System.out.println(whiteColor + "-------");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("\033[0;31m" + e.getMessage());
        }
    }

    private static void printTree(SyntaxNode node) {
        printTree(node, "", false);
    }

    private static void printTree(SyntaxNode node, String indent, boolean isLast) {
        String marker = isLast ? "└──" : "├──";
        System.out.print(indent);
        System.out.print(marker);
        System.out.print(node.getKind());
        if (node instanceof SyntaxToken s && s.getValue() != null) {
            System.out.print(" ");
            System.out.print(s.getValue());
        }
        System.out.println();

        indent += isLast ? "    " : "│   ";

        SyntaxNode last = null;
        if (!node.getChildren().isEmpty()) {
            last = node.getChildren().get(node.getChildren().size() - 1);
        }
        for (SyntaxNode n : node.getChildren()) {
            printTree(n, indent, last == n);
        }


    }
}
