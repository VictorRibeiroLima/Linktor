package codeanalysis.evaluator;

import codeanalysis.source.TextSpan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class AnnotatedText {
    private final String text;

    private final List<TextSpan> spans;

    public AnnotatedText(String text, List<TextSpan> spans) {
        this.text = text;
        this.spans = List.copyOf(spans);
    }

    public String getText() {
        return text;
    }

    public List<TextSpan> getSpans() {
        return spans;
    }

    public static AnnotatedText parse(String text) {
        StringBuilder result = new StringBuilder();
        List<TextSpan> spans = new ArrayList<>();
        Stack<Integer> startStack = new Stack<>();
        int position = 0;

        for (char c : text.toCharArray()) {
            if (c == '[') {
                startStack.push(position);
            } else if (c == ']') {
                int start = startStack.pop();
                int end = position - start;
                TextSpan span = new TextSpan(start, end);
                spans.add(span);
            } else {
                position++;
                result.append(c);
            }
        }
        return new AnnotatedText(result.toString(), List.copyOf(spans));
    }

    private static String unindent(String text) throws IOException {
        text = unindent(text);
        List<String> lines = new BufferedReader(new StringReader(text)).lines().toList();
        int minIndentation = 2147483647;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.trim().length() == 0) {
                lines.add(i, "");
                continue;
            }


            int indentation = line.length() - line.trim().length();
            minIndentation = Math.min(minIndentation, indentation);
        }
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(0).length() == 0)
                continue;
            lines.add(i, lines.get(i).substring(minIndentation));
        }

        while (lines.size() > 0 && lines.get(0).length() == 0) {
            lines.remove(0);
        }
        while (lines.size() > 0 && lines.get(lines.size() - 1).length() == 0) {
            lines.remove(lines.size() - 1);
        }


        return String.join(System.lineSeparator(), lines);
    }
}
