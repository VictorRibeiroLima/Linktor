import codeanalysis.binding.Binder;
import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.evaluator.Evaluator;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;
import codeanalysis.syntax.SyntaxTree;

import java.util.List;
import java.util.Scanner;

public class Linktor {
    public static void main(String[] args) {
        try {
            boolean showTree = false;
            Scanner console = new Scanner(System.in);
            while (true) {
                String input = console.nextLine();
                if (input.equals("#showTree")) {
                    showTree = true;
                    console.nextLine();
                    continue;
                }
                SyntaxTree tree = SyntaxTree.parse(input);
                List<String> diagnostics = tree.getDiagnostics();
                Binder binder = new Binder();

                BoundExpression bound = binder.bindExpression(tree.getRoot());
                diagnostics.addAll(binder.getDiagnostics());
                if (showTree) {
                    printTree(tree.getRoot());
                }
                if (!tree.getDiagnostics().isEmpty()) {
                    for (String error : tree.getDiagnostics()) {
                        System.out.println("\033[0;31m" + error);
                    }
                } else {
                    Evaluator evaluator = new Evaluator(bound);
                    Object result = evaluator.evaluate();
                    System.out.println("Result: " + result);

                }
                System.out.println("\033[0m" + "-------");
            }
        } catch (Exception e) {
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
