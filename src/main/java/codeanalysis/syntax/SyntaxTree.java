package codeanalysis.syntax;

import codeanalysis.diagnostics.DiagnosticBag;
import codeanalysis.parser.Parser;
import codeanalysis.syntax.expression.ExpressionSyntax;

public class SyntaxTree {
    private final DiagnosticBag diagnostics = new DiagnosticBag();
    private final ExpressionSyntax root;
    private final SyntaxToken endOfFileToken;

    public SyntaxTree(ExpressionSyntax root, SyntaxToken endOfFileToken, DiagnosticBag diagnostics) {
        this.root = root;
        this.endOfFileToken = endOfFileToken;
        this.diagnostics.addAll(diagnostics);
    }

    public static SyntaxTree parse(String input) {
        Parser parser = new Parser(input);
        SyntaxTree tree = parser.parse();
        return tree;
    }

    public DiagnosticBag getDiagnostics() {
        return diagnostics;
    }

    public ExpressionSyntax getRoot() {
        return root;
    }

    public SyntaxToken getEndOfFileToken() {
        return endOfFileToken;
    }
}
