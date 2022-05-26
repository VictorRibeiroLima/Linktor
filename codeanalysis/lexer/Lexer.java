package codeanalysis.lexer;

import codeanalysis.syntax.SyntaxFacts;
import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxToken;

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
            return new SyntaxToken(SyntaxKind.END_OF_FILE_TOKEN, position, String.valueOf(getCurrent()), null);
        if (Character.isDigit(getCurrent())) {
            int start = position;

            while (Character.isDigit(getCurrent()))
                next();

            String text = this.text.substring(start, position);
            try {
                return new SyntaxToken(SyntaxKind.NUMBER_TOKEN, start, text, Integer.parseInt(text));
            } catch (NumberFormatException e) {
                diagnostics.add("ERROR: The number " + text + "is not a valid representation of Int32");
                return new SyntaxToken(SyntaxKind.NUMBER_TOKEN, start, text, null);
            }

        }
        if (Character.isWhitespace(getCurrent())) {
            int start = position;

            while (Character.isWhitespace(getCurrent()))
                next();


            String text = this.text.substring(start, position);
            return new SyntaxToken(SyntaxKind.WHITESPACE_TOKEN, start, text, null);
        }
        if (Character.isLetter(getCurrent())) {
            int start = position;

            while (Character.isLetter(getCurrent()))
                next();

            String text = this.text.substring(start, position);
            SyntaxKind kind = SyntaxFacts.getKeywordKind(text);
            return new SyntaxToken(kind, start, text, null);

        }

        return new SyntaxToken(SyntaxKind.get(getCurrent()), position++, text.substring(position - 1, position), null);
    }


}
