package codeanalysis.lexer;

import codeanalysis.diagnostics.DiagnosticBag;
import codeanalysis.diagnostics.text.SourceText;
import codeanalysis.syntax.SyntaxFacts;
import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxToken;


public final class Lexer {
    private final SourceText text;
    private int position;

    private int start;

    private SyntaxKind kind;

    private Object value;

    private final DiagnosticBag diagnostics = new DiagnosticBag();


    public Lexer(SourceText text) {
        this.text = text;
        this.position = 0;
    }

    public DiagnosticBag getDiagnostics() {
        return diagnostics;
    }


    public SyntaxToken lex() {
        start = position;
        kind = SyntaxKind.BAD_TOKEN;
        value = null;
        switch (getCurrent()) {
            case '\0':
                kind = SyntaxKind.END_OF_FILE_TOKEN;
                break;
            case '+':
                kind = SyntaxKind.PLUS_TOKEN;
                next();
                break;
            case '-':
                kind = SyntaxKind.MINUS_TOKEN;
                next();
                break;
            case '/':
                kind = SyntaxKind.SLASH_TOKEN;
                next();
                break;
            case '*':
                kind = SyntaxKind.STAR_TOKEN;
                next();
                break;
            case '(':
                kind = SyntaxKind.OPEN_PARENTHESIS_TOKEN;
                next();
                break;
            case ')':
                kind = SyntaxKind.CLOSE_PARENTHESIS_TOKEN;
                next();
                break;
            case '=': {
                if (lookahead() == '=') {
                    kind = SyntaxKind.EQUAL_EQUAL_TOKEN;
                    next();
                } else {
                    kind = SyntaxKind.EQUAL_TOKEN;
                }
                next();
                break;
            }
            case '<': {
                if (lookahead() == '=') {
                    kind = SyntaxKind.LESS_EQUAL_TOKEN;
                    next();
                } else {
                    kind = SyntaxKind.LESS_TOKEN;
                }
                next();
                break;
            }
            case '>': {
                if (lookahead() == '=') {
                    kind = SyntaxKind.GREATER_EQUAL_TOKEN;
                    next();
                } else {
                    kind = SyntaxKind.GREATER_TOKEN;
                }
                next();
                break;
            }
            case '!': {
                if (lookahead() == '=') {
                    kind = SyntaxKind.EXCLAMATION_EQUAL_TOKEN;
                    next();
                } else {
                    kind = SyntaxKind.EXCLAMATION_TOKEN;
                }
                next();
                break;
            }
            case '&': {
                if (lookahead() == '&') {
                    kind = SyntaxKind.AMPERSAND_AMPERSAND_TOKEN;
                    next();
                }
                next();
                break;
            }
            case '|': {
                if (lookahead() == '|') {
                    kind = SyntaxKind.PIPE_PIPE_TOKEN;
                    next();
                }
                next();
                break;
            }
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                readNumberToken();
                break;
            case ' ':
            case '\t':
            case '\n':
            case '\r':
                readWhitespace();
                break;
            default:
                if (Character.isLetter(getCurrent()))
                    readWordToken();
                else if (Character.isWhitespace(getCurrent()))
                    readWhitespace();
                else {
                    diagnostics.reportBadChar(position, getCurrent());
                    next();
                }
                break;
        }

        String text = SyntaxFacts.getText(kind);
        if (text == null) {
            text = this.text.toString(start, position);
        }
        return new SyntaxToken(kind, start, text, value);
    }

    private void readNumberToken() {
        while (Character.isDigit(getCurrent()))
            next();

        String text = this.text.toString(start, position);

        kind = SyntaxKind.NUMBER_TOKEN;
        value = Integer.parseInt(text);
    }

    private void readWhitespace() {
        while (Character.isWhitespace(getCurrent()))
            next();
        kind = SyntaxKind.WHITESPACE_TOKEN;
    }

    private void readWordToken() {
        while (Character.isLetter(getCurrent()))
            next();

        String text = this.text.toString(start, position);
        kind = SyntaxFacts.getKeywordKind(text);
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
