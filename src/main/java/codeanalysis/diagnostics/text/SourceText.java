package codeanalysis.diagnostics.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SourceText {

    private final List<TextLine> lines;
    private final String text;

    private SourceText(String text) {
        this.text = text;
        List<TextLine> lines = SourceText.parseLines(this, text);
        this.lines = Collections.unmodifiableList(lines);
    }

    public int getLineIndex(int position) {
        return getLineIndex(lines, position, 0, this.lines.size());
    }

    public List<TextLine> getLines() {
        return lines;
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
        return toString(span.start(), span.end());
    }

    public static SourceText from(String text) {
        return new SourceText(text);
    }

    private static List<TextLine> parseLines(SourceText source, String text) {
        List<TextLine> lines = new ArrayList<>();
        int lineStartingPosition = 0;
        int position = 0;
        while (position < text.length()) {
            int lineBreakWidth = getLineBreakWidth(text, position);
            if (lineBreakWidth == 0) {
                position++;
            } else {
                addLine(source, lineStartingPosition, position, lineBreakWidth, lines);
                position += lineBreakWidth;
                lineStartingPosition = position;
            }
        }
        if (position > lineStartingPosition) {
            addLine(source, lineStartingPosition, position, 0, lines);
        }
        return lines;
    }

    private static void addLine(SourceText source, int lineStartingPosition, int position, int lineBreakWidth, List<TextLine> lines) {
        int lineLength = position - lineStartingPosition;
        int lineLengthWithLineBreak = lineLength + lineBreakWidth;
        TextLine line = new TextLine(source, lineStartingPosition, lineLength, lineLengthWithLineBreak);
        lines.add(line);
    }

    private static int getLineBreakWidth(String text, int i) {
        char c = text.charAt(i);
        char lookAhead = i >= text.length() - 1 ? '\0' : text.charAt(i + 1);
        if (c == '\r' && lookAhead == '\n')
            return 2;
        if (c == '\r' || c == '\n')
            return 1;
        return 0;
    }

    private int getLineIndex(List<TextLine> lines, int target, int start, int end) {
        int mid = start + (end - start) / 2;
        if (start > end)
            return -1;
        if (lines.get(mid).getStart() <= target && lines.get(mid).getEndWithLineBreak() >= target)
            return mid;

        if (lines.get(mid).getEnd() < target)
            return getLineIndex(lines, target, mid + 1, end);

        return getLineIndex(lines, target, start, mid - 1);
    }
}
