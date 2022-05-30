package codeanalysis.diagnostics.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SourceText {

    private final List<TextLine> lines;
    private final String text;

    private SourceText(String text) {
        this.text = text;
        List<TextLine> lines = parseLines(text);
        this.lines = Collections.unmodifiableList(lines);
    }

    public int getLineFromIndex(int position) {
        return getLineFromIndex(lines, position, 0, this.text.length() - 1);
    }

    public char charAt(int index) {
        return text.charAt(index);
    }

    public int length() {
        return text.length();
    }

    @Override
    public String toString() {
        return this.text;
    }

    public String toString(int start, int end) {
        return this.text.substring(start, end);
    }

    public String toString(TextSpan span) {
        return toString(span.start(), span.length());
    }

    public static SourceText from(String text) {
        return new SourceText(text);
    }

    private List<TextLine> parseLines(String text) {
        List<TextLine> lines = new ArrayList<>();
        int lineStartingPosition = 0;
        int position = 0;
        while (position < text.length()) {
            int lineBreakWidth = getLineBreakWidth(text, position);
            if (lineBreakWidth == 0) {
                position++;
            } else {
                addLine(lineStartingPosition, position, lineBreakWidth, lines);
                position += lineBreakWidth;
                lineStartingPosition = position;
            }
        }
        if (position > lineStartingPosition) {
            addLine(position, lineStartingPosition, 0, lines);
        }
        return lines;
    }

    private void addLine(int lineStartingPosition, int position, int lineBreakWidth, List<TextLine> lines) {
        int lineLength = position - lineStartingPosition;
        int lineLengthWithLineBreak = lineLength + lineBreakWidth;
        TextLine line = new TextLine(this, lineStartingPosition, lineLength, lineLengthWithLineBreak);
        lines.add(line);
    }

    private int getLineBreakWidth(String text, int i) {
        char c = text.charAt(i);
        char lookAhead = i >= text.length() ? '\0' : text.charAt(i + 1);
        if (c == '\r' && c == '\n')
            return 2;
        if (c == '\r' || c == '\n')
            return 1;
        return 0;
    }

    private int getLineFromIndex(List<TextLine> lines, int target, int start, int end) {
        int mid = start + (end - start) / 2;
        if (start > end)
            return -1;
        if (lines.get(mid).getStart() == target)
            return mid;

        if (lines.get(mid).getEnd() > target)
            return getLineFromIndex(lines, target, mid + 1, end);

        return getLineFromIndex(lines, target, start, mid - 1);
    }
}
