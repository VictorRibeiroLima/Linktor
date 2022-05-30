package codeanalysis.diagnostics;

import codeanalysis.diagnostics.text.TextSpan;

public record Diagnostic(TextSpan span, String message) {

    @Override
    public String toString() {
        return message;
    }
}
