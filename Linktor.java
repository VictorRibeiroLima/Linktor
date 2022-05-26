import codeanalysis.evaluator.Evaluator;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;
import codeanalysis.syntax.SyntaxTree;

import java.util.Scanner;

public class Linktor {
    public static void main(String[] args) {
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
            if (showTree) {
                printTree(tree.getRoot());
            }
            if (!tree.getDiagnostics().isEmpty()) {
                for (String error : tree.getDiagnostics()) {
                    System.out.println("\033[0;31m" + error);
                }
            } else {
                Evaluator evaluator = new Evaluator(tree.getRoot());
                try {
                    int result = evaluator.evaluate();
                    System.out.println("Result: " + result);
                } catch (Exception e) {
                    System.out.println("\033[0;31m" + e.getMessage());
                }

            }
            System.out.println("\033[0m" + "-------");
        }

    }

    private static void printTree(SyntaxNode node) {
        printTree(node, "", false);
    }

    private static void printTree(SyntaxNode node, String indent, boolean isLast) {
        String marker = isLast ? "└──" : "├──";
        System.out.print(indent);
        System.out.print(marker);
        System.out.print(node.getType());
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
