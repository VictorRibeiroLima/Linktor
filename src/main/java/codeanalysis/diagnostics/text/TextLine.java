package codeanalysis.diagnostics.text;

public class TextLine {
    private final SourceText text;
    private final int start;
    private final int length;
    private final int lengthWithLineBreak;
    private final int end;
    private final int endWithLineBreak;
    private final TextSpan span;
    private final TextSpan spanWithLineBreak;

    public TextLine(SourceText text, int start, int length, int lengthWithLineBreak) {
        this.text = text;
        this.start = start;
        this.length = length;
        this.lengthWithLineBreak = lengthWithLineBreak;
        this.end = this.start + this.length;
        this.endWithLineBreak = this.start + lengthWithLineBreak;
        this.span = new TextSpan(this.start, this.length);
        this.spanWithLineBreak = new TextSpan(this.start, this.lengthWithLineBreak);
    }

    public SourceText getText() {
        return text;
    }

    public int getStart() {
        return start;
    }

    public int getLength() {
        return length;
    }

    public int getLengthWithLineBreak() {
        return lengthWithLineBreak;
    }

    public int getEnd() {
        return end;
    }

    public int getEndWithLineBreak() {
        return endWithLineBreak;
    }

    public TextSpan getSpan() {
        return span;
    }

    public TextSpan getSpanWithLineBreak() {
        return spanWithLineBreak;
    }

    @Override
    public String toString() {
        return this.text.toString();
    }
}
