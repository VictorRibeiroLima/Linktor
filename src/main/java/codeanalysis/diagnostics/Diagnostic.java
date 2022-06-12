package codeanalysis.diagnostics;

import codeanalysis.source.TextLocation;

public record Diagnostic(TextLocation location, String message) {

    @Override
    public String toString() {
        return message;
    }
}
