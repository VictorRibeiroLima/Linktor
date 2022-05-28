package codeanalysis.diagnostics;

public record TextSpan(int start, int length) {
    public int end() {
        return start + length;
    }
}
