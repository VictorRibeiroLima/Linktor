package codeanalysis.syntax;

import codeanalysis.diagnostics.DiagnosticBag;
import codeanalysis.diagnostics.text.SourceText;
import codeanalysis.diagnostics.text.TextSpan;
import codeanalysis.lexer.Lexer;
import codeanalysis.parser.Parser;

import java.util.ArrayList;
import java.util.List;

public class SyntaxTree {
    private final DiagnosticBag diagnostics = new DiagnosticBag();
    private final CompilationUnitSyntax root;

    private final SourceText text;

    private SyntaxTree(SourceText text) {
        Parser parser = new Parser(text);
        CompilationUnitSyntax root = parser.parseCompilationUnit();

        this.root = root;
        this.diagnostics.addAll(parser.getDiagnostics());
        this.text = text;
    }

    public static SyntaxTree parse(String input) {
        SourceText text = SourceText.from(input);
        return parse(text);
    }

    public static SyntaxTree parse(SourceText text) {
        return new SyntaxTree(text);
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

    public CompilationUnitSyntax getRoot() {
        return root;
    }

    public SourceText getText() {
        return text;
    }

    public String toString(TextSpan span) {
        var text = this.text.toString();
        return text.substring(span.start(), span.length());
    }
}
