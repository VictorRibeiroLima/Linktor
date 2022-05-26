package codeanalysis.syntax;

import codeanalysis.parser.Parser;
import codeanalysis.syntax.expression.ExpressionSyntax;

import java.util.ArrayList;
import java.util.List;

public class SyntaxTree {
    private final List<String> diagnostics = new ArrayList<>();
    private final ExpressionSyntax root;
    private final SyntaxToken endOfFileToken;

    public SyntaxTree(ExpressionSyntax root, SyntaxToken endOfFileToken, List<String> diagnostics) {
        this.root = root;
        this.endOfFileToken = endOfFileToken;
        this.diagnostics.addAll(diagnostics);
    }

    public static SyntaxTree parse(String input) {
        Parser parser = new Parser(input);
        SyntaxTree tree = parser.parse();
        return tree;
    }

    public List<String> getDiagnostics() {
        return diagnostics;
    }

    public ExpressionSyntax getRoot() {
        return root;
    }

    public SyntaxToken getEndOfFileToken() {
        return endOfFileToken;
    }
}
