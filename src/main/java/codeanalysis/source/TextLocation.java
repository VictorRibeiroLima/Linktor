package codeanalysis.source;

public record TextLocation(SourceText text, TextSpan span) {

    public int startLine() {
        return text.getLineIndex(span.start());
    }

    public int startChar() {
        return span.start() - text.getLines().get(startLine()).getStart();
    }

    public int endLine() {
        return text.getLineIndex(span.end());
    }


    public int endChar() {
        return span.end() - text.getLines().get(endLine()).getEnd();
    }
}
