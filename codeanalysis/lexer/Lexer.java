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

    public List<String> getDiagnostics() {
        return diagnostics;
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
                diagnostics.add("ERROR: The number " + text + "is not a valid representation of Integer");
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
        switch (getCurrent()) {
            case '+':
                return new SyntaxToken(SyntaxKind.PLUS_TOKEN, position++, "+", null);
            case '-':
                return new SyntaxToken(SyntaxKind.MINUS_TOKEN, position++, "-", null);
            case '/':
                return new SyntaxToken(SyntaxKind.SLASH_TOKEN, position++, "/", null);
            case '*':
                return new SyntaxToken(SyntaxKind.STAR_TOKEN, position++, "*", null);
            case '(':
                return new SyntaxToken(SyntaxKind.OPEN_PARENTHESIS_TOKEN, position++, "(", null);
            case ')':
                return new SyntaxToken(SyntaxKind.CLOSE_PARENTHESIS_TOKEN, position++, ")", null);
            case '=': {
                if (lookahead() == '=')
                    return new SyntaxToken(SyntaxKind.EQUAL_EQUAL_TOKEN, position += 2, "==", null);
                return new SyntaxToken(SyntaxKind.EQUAL_TOKEN, position++, "=", null);
            }
            case '!': {
                if (lookahead() == '=')
                    return new SyntaxToken(SyntaxKind.EXCLAMATION_EQUAL_TOKEN, position += 2, "!=", null);
                return new SyntaxToken(SyntaxKind.EXCLAMATION_TOKEN, position++, "!", null);
            }
            case '&': {
                if (lookahead() == '&')
                    return new SyntaxToken(SyntaxKind.AMPERSAND_AMPERSAND_TOKEN, position += 2, "&&", null);
                break;
            }
            case '|': {
                if (lookahead() == '|')
                    return new SyntaxToken(SyntaxKind.PIPE_PIPE_TOKEN, position += 2, "||", null);
                break;
            }

        }
        diagnostics.add("ERROR: bad char input :" + getCurrent());
        return new SyntaxToken(SyntaxKind.BAD_TOKEN, position++, text.substring(position - 1, position), null);
    }

    private void next() {
        this.position++;
    }

    private char getCurrent() {
        return peek(0);
    }

    private char peek(int offset) {
        int index = position + offset;
        if (index >= text.length())
            return '\0';
        return text.charAt(index);
    }

    private char lookahead() {
        return peek(1);
    }
}
