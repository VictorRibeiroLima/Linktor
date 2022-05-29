package codeanalysis.diagnostics.text;

public record TextSpan(int start, int length) {
    public static TextSpan fromBounds(int start, int end) {
        int length = end - start;
        return new TextSpan(start, length);
    }

    public int end() {
        return start + length;
    }
}
