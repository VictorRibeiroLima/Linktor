package codeanalysis.lexer;

import codeanalysis.syntax.SyntaxToken;
import codeanalysis.syntax.SyntaxType;

import java.util.ArrayList;
import java.util.List;


public final class Lexer {
    private final String text;
    private int position;

    private final List<String> diagnostics = new ArrayList<>();


    public Lexer(String text) {
        this.text = text;
        this.position = 0;
    }

    public char getCurrent() {
        if (position >= text.length())
            return '\0';
        return text.charAt(position);
    }

    public List<String> getDiagnostics() {
        return diagnostics;
    }

    private void next() {
        this.position++;
    }

    public SyntaxToken lex() {
        if (getCurrent() == '\0')
            return new SyntaxToken(SyntaxType.END_OF_FILE_TOKEN, position, String.valueOf(getCurrent()), null);
        if (Character.isDigit(getCurrent())) {
            int start = position;

            while (Character.isDigit(getCurrent()))
                next();

            String text = this.text.substring(start, position);
            try {
                return new SyntaxToken(SyntaxType.NUMBER_TOKEN, start, text, Integer.parseInt(text));
            } catch (NumberFormatException e) {
                diagnostics.add("ERROR: The number " + text + "is not a valid representation of Int32");
                return new SyntaxToken(SyntaxType.NUMBER_TOKEN, start, text, null);
            }

        }
        if (Character.isWhitespace(getCurrent())) {
            int start = position;

            while (Character.isWhitespace(getCurrent()))
                next();


            String text = this.text.substring(start, position);
            return new SyntaxToken(SyntaxType.WHITESPACE_TOKEN, start, text, null);
        }

        return new SyntaxToken(SyntaxType.get(getCurrent()), position++, text.substring(position - 1, position), null);
    }


}
