package codeanalysis.syntax;

import codeanalysis.diagnostics.DiagnosticBag;
import codeanalysis.diagnostics.text.SourceText;
import codeanalysis.lexer.Lexer;
import codeanalysis.parser.Parser;
import codeanalysis.syntax.expression.ExpressionSyntax;

import java.util.ArrayList;
import java.util.List;

public class SyntaxTree {
    private final DiagnosticBag diagnostics = new DiagnosticBag();
    private final ExpressionSyntax root;
    private final SyntaxToken endOfFileToken;

    private final SourceText text;

    public SyntaxTree(ExpressionSyntax root, SyntaxToken endOfFileToken, DiagnosticBag diagnostics, SourceText text) {
        this.root = root;
        this.endOfFileToken = endOfFileToken;
        this.diagnostics.addAll(diagnostics);
        this.text = text;
    }

    public static SyntaxTree parse(String input) {
        SourceText text = SourceText.from(input);
        return parse(text);
    }

    public static SyntaxTree parse(SourceText text) {
        final Parser parser = new Parser(text);
        return parser.parse();
    }

    public static List<SyntaxToken> parseTokens(String input) {
        SourceText text = SourceText.from(input);
        final Lexer lexer = new Lexer(text);
        final List<SyntaxToken> tokens = new ArrayList<>();
        while (true) {
            SyntaxToken token = lexer.lex();
            if (token.getKind() == SyntaxKind.END_OF_FILE_TOKEN)
                break;
            tokens.add(token);
        }
        return tokens;
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

    public SourceText getText() {
        return text;
    }
}
