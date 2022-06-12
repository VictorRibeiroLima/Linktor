package io;

import codeanalysis.diagnostics.Diagnostic;
import codeanalysis.source.SourceText;
import codeanalysis.source.TextLine;
import codeanalysis.syntax.SyntaxTree;
import util.ConsoleColors;

import java.io.PrintWriter;
import java.util.List;

public class DiagnosticsWriter {
    private DiagnosticsWriter() {
    }


    public static void writeTo(PrintWriter out, List<Diagnostic> diagnostics, SyntaxTree tree) {
        writeTo(out, diagnostics, tree, false);
    }

    public static void writeTo(PrintWriter out, List<Diagnostic> diagnostics, SyntaxTree tree, boolean paint) {
        if (!diagnostics.isEmpty()) {
            SourceText text = tree.getText();
            for (Diagnostic diagnostic : diagnostics) {
                var filePath = diagnostic.location().fileName();
                var span = diagnostic.location().span();
                int lineIndex = text.getLineIndex(span.start());
                TextLine line = text.getLines().get(lineIndex);
                int lineNumber = lineIndex + 1;
                int character = span.start() - line.getStart() + 1;

                if (paint)
                    out.println(ConsoleColors.RED);
                out.println(diagnostic);

                String prefix = text.toString().substring(line.getStart(), span.start());
                String error = tree.getText().toString(span);
                String suffix = text.toString().substring(span.end());

                out.println(filePath + ":" + lineNumber + ":" + character);
                if (paint)
                    out.println(ConsoleColors.WHITE + prefix + ConsoleColors.RED + error + ConsoleColors.WHITE + suffix);
                else
                    out.println(prefix + error + suffix);
                out.flush();
                out.close();
            }
        }
    }
}
