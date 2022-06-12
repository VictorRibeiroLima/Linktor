package codeanalysis.syntax;

import codeanalysis.diagnostics.Diagnostic;
import codeanalysis.source.SourceText;
import codeanalysis.source.TextSpan;
import codeanalysis.source.handler.IParseHandler;
import codeanalysis.source.handler.ParserHandler;
import codeanalysis.source.handler.TokenParserHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class SyntaxTree {
    private final List<Diagnostic> diagnostics;
    private final CompilationUnitSyntax root;

    private final SourceText text;

    private SyntaxTree(SourceText text, IParseHandler handler) {
        this.text = text;
        handler.handle(this);
        this.root = handler.getRoot();
        this.diagnostics = List.copyOf(handler.getDiagnostics());
    }

    public static SyntaxTree load(String fileName) throws FileNotFoundException {
        var file = new File(fileName);
        var br = new BufferedReader(new FileReader(file));

        var builder = new StringBuilder();
        br.lines().forEach(line -> {
            builder.append(line);
            builder.append("\n");
        });
        var text = builder.toString();
        var sourceText = SourceText.from(text, fileName);
        return parse(sourceText);
    }

    public static SyntaxTree parse(String input) {
        SourceText text = SourceText.from(input);
        return parse(text);
    }

    public static SyntaxTree parse(SourceText text) {
        return new SyntaxTree(text, new ParserHandler());
    }

    public static List<SyntaxToken> parseTokens(String input) {
        var handler = new TokenParserHandler();
        var text = SourceText.from(input);
        new SyntaxTree(text, handler);
        return handler.getTokens();
    }

    public List<Diagnostic> getDiagnostics() {
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
