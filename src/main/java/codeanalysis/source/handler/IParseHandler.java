package codeanalysis.source.handler;

import codeanalysis.diagnostics.Diagnostic;
import codeanalysis.syntax.CompilationUnitSyntax;
import codeanalysis.syntax.SyntaxTree;

import java.util.List;

public interface IParseHandler {
    void handle(SyntaxTree tree);

    CompilationUnitSyntax getRoot();

    List<Diagnostic> getDiagnostics();
}
