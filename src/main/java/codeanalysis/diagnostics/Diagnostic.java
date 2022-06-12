package codeanalysis.diagnostics;

import codeanalysis.source.TextSpan;

public record Diagnostic(TextSpan span, String message) {

    @Override
    public String toString() {
        return message;
    }
}
