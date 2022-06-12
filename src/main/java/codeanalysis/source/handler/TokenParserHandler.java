package codeanalysis.source.handler;

import codeanalysis.diagnostics.Diagnostic;
import codeanalysis.lexer.Lexer;
import codeanalysis.syntax.CompilationUnitSyntax;
import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxToken;
import codeanalysis.syntax.SyntaxTree;

import java.util.ArrayList;
import java.util.List;

public class TokenParserHandler implements IParseHandler {

    private CompilationUnitSyntax root;
    private List<Diagnostic> diagnostics;

    private final List<SyntaxToken> tokens = new ArrayList<>();

    @Override
    public void handle(SyntaxTree tree) {
        var lexer = new Lexer(tree);
        while (true) {
            var token = lexer.lex();
            if (token.getKind() == SyntaxKind.END_OF_FILE_TOKEN) {
                root = new CompilationUnitSyntax(List.of(), token);
                break;
            }
            tokens.add(token);
        }
        diagnostics = lexer.getDiagnostics().toUnmodifiableList();
    }

    @Override
    public CompilationUnitSyntax getRoot() {
        return root;
    }

    @Override
    public List<Diagnostic> getDiagnostics() {
        return diagnostics;
    }

    public List<SyntaxToken> getTokens() {
        return tokens;
    }
}
