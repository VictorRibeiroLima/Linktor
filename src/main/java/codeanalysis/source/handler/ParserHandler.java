package codeanalysis.source.handler;

import codeanalysis.diagnostics.Diagnostic;
import codeanalysis.parser.Parser;
import codeanalysis.syntax.CompilationUnitSyntax;
import codeanalysis.syntax.SyntaxTree;

import java.util.List;

public class ParserHandler implements IParseHandler {
    private CompilationUnitSyntax root;
    private List<Diagnostic> diagnostics;

    @Override
    public void handle(SyntaxTree tree) {
        var parser = new Parser(tree);
        root = parser.parseCompilationUnit();
        diagnostics = List.copyOf(parser.getDiagnostics().getDiagnostics());
    }

    @Override
    public CompilationUnitSyntax getRoot() {
        return root;
    }

    @Override
    public List<Diagnostic> getDiagnostics() {
        return diagnostics;
    }
}
