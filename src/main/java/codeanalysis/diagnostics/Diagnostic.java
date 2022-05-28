package codeanalysis.diagnostics;

public record Diagnostic(TextSpan span, String message) {

    @Override
    public String toString() {
        return message;
    }
}
