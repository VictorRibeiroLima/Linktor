package io;

import codeanalysis.diagnostics.Diagnostic;
import codeanalysis.source.SourceText;
import codeanalysis.source.TextLine;
import codeanalysis.syntax.SyntaxTree;
import util.ConsoleColors;

import java.util.List;

public class DiagnosticsWriter {
    private DiagnosticsWriter() {
    }


    public static void write(List<Diagnostic> diagnostics, SyntaxTree tree) {
        if (!diagnostics.isEmpty()) {
            SourceText text = tree.getText();
            for (Diagnostic diagnostic : diagnostics) {
                var filePath = diagnostic.location().fileName();
                var span = diagnostic.location().span();
                int lineIndex = text.getLineIndex(span.start());
                TextLine line = text.getLines().get(lineIndex);
                int lineNumber = lineIndex + 1;
                int character = span.start() - line.getStart() + 1;

                System.out.println(ConsoleColors.RED);
                System.out.println(diagnostic);

                String prefix = text.toString().substring(line.getStart(), span.start());
                String error = tree.getText().toString(span);
                String suffix = text.toString().substring(span.end());

                System.out.println(filePath + ":" + lineNumber + ":" + character);
                System.out.println(ConsoleColors.WHITE + prefix + ConsoleColors.RED + error + ConsoleColors.WHITE + suffix);
            }
        }
    }
}
