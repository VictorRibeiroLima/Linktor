package src.codeanalysis.lexer;

import src.codeanalysis.diagnostics.DiagnosticBag;
import src.codeanalysis.diagnostics.TextSpan;
import src.codeanalysis.syntax.SyntaxFacts;
import src.codeanalysis.syntax.SyntaxKind;
import src.codeanalysis.syntax.SyntaxToken;


public final class Lexer {
    private final String text;
    private int position;

    private final DiagnosticBag diagnostics = new DiagnosticBag();


    public Lexer(String text) {
        this.text = text;
        this.position = 0;
    }

    public DiagnosticBag getDiagnostics() {
        return diagnostics;
    }


    public SyntaxToken lex() {
        if (getCurrent() == '\0')
            return new SyntaxToken(SyntaxKind.END_OF_FILE_TOKEN, position, String.valueOf(getCurrent()), null);
        int start = position;

        if (Character.isDigit(getCurrent())) {

            while (Character.isDigit(getCurrent()))
                next();

            String text = this.text.substring(start, position);
            try {
                return new SyntaxToken(SyntaxKind.NUMBER_TOKEN, start, text, Integer.parseInt(text));
            } catch (NumberFormatException e) {
                diagnostics.reportInvalidType(new TextSpan(start, position), this.text, Integer.class);
                return new SyntaxToken(SyntaxKind.NUMBER_TOKEN, start, text, null);
            }

        }
        if (Character.isWhitespace(getCurrent())) {

            while (Character.isWhitespace(getCurrent()))
                next();


            String text = this.text.substring(start, position);
            return new SyntaxToken(SyntaxKind.WHITESPACE_TOKEN, start, text, null);
        }
        if (Character.isLetter(getCurrent())) {

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
                if (lookahead() == '=') {
                    position += 2;
                    return new SyntaxToken(SyntaxKind.EQUAL_EQUAL_TOKEN, start, "==", null);
                }
                return new SyntaxToken(SyntaxKind.EQUAL_TOKEN, position++, "=", null);
            }
            case '!': {
                if (lookahead() == '=') {
                    position += 2;
                    return new SyntaxToken(SyntaxKind.EXCLAMATION_EQUAL_TOKEN, start, "!=", null);
                }
                return new SyntaxToken(SyntaxKind.EXCLAMATION_TOKEN, position++, "!", null);
            }
            case '&': {
                if (lookahead() == '&') {
                    position += 2;
                    return new SyntaxToken(SyntaxKind.AMPERSAND_AMPERSAND_TOKEN, start, "&&", null);
                }
                break;
            }
            case '|': {
                if (lookahead() == '|') {
                    position += 2;
                    return new SyntaxToken(SyntaxKind.PIPE_PIPE_TOKEN, start, "||", null);
                }
                break;
            }

        }
        diagnostics.reportBadChar(position, getCurrent());
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
