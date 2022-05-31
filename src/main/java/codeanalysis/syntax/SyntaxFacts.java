package codeanalysis.syntax;

import java.util.ArrayList;
import java.util.List;

public final class SyntaxFacts {
    private SyntaxFacts() {

    }

    public static int getUnaryOperatorPrecedence(SyntaxKind kind) {
        switch (kind) {
            case PLUS_TOKEN:
            case MINUS_TOKEN:
            case EXCLAMATION_TOKEN:
                return 6;
            default:
                return 0;
        }
    }

    public static int getBinaryOperatorPrecedence(SyntaxKind kind) {
        switch (kind) {
            case STAR_TOKEN:
            case SLASH_TOKEN:
                return 5;
            case PLUS_TOKEN:
            case MINUS_TOKEN:
                return 4;
            case EQUAL_EQUAL_TOKEN:
            case EXCLAMATION_EQUAL_TOKEN:
            case LESS_TOKEN:
            case LESS_EQUAL_TOKEN:
            case GREATER_TOKEN:
            case GREATER_EQUAL_TOKEN:
                return 3;
            case AMPERSAND_AMPERSAND_TOKEN:
                return 2;
            case PIPE_PIPE_TOKEN:
                return 1;
            default:
                return 0;
        }
    }

    public static SyntaxKind getKeywordKind(String text) {
        switch (text) {
            case "true":
                return SyntaxKind.TRUE_KEYWORD;
            case "false":
                return SyntaxKind.FALSE_KEYWORD;
            case "var":
                return SyntaxKind.VAR_KEYWORD;
            case "let":
                return SyntaxKind.LET_KEYWORD;
            default:
                return SyntaxKind.IDENTIFIER_TOKEN;

        }
    }

    public static String getText(SyntaxKind kind) {
        switch (kind) {
            case PLUS_TOKEN:
                return "+";
            case MINUS_TOKEN:
                return "-";
            case SLASH_TOKEN:
                return "/";
            case STAR_TOKEN:
                return "*";
            case OPEN_PARENTHESIS_TOKEN:
                return "(";
            case CLOSE_PARENTHESIS_TOKEN:
                return ")";
            case OPEN_BRACE_TOKEN:
                return "{";
            case CLOSE_BRACE_TOKEN:
                return "}";
            case EQUAL_TOKEN:
                return "=";
            case EQUAL_EQUAL_TOKEN:
                return "==";
            case EXCLAMATION_EQUAL_TOKEN:
                return "!=";
            case GREATER_TOKEN:
                return ">";
            case GREATER_EQUAL_TOKEN:
                return ">=";
            case LESS_TOKEN:
                return "<";
            case LESS_EQUAL_TOKEN:
                return "<=";
            case EXCLAMATION_TOKEN:
                return "!";
            case AMPERSAND_AMPERSAND_TOKEN:
                return "&&";
            case PIPE_PIPE_TOKEN:
                return "||";
            case FALSE_KEYWORD:
                return "false";
            case TRUE_KEYWORD:
                return "true";
            case VAR_KEYWORD:
                return "var";
            case LET_KEYWORD:
                return "let";
            default:
                return null;
        }
    }

    public static List<SyntaxKind> getUnaryOperatorKinds() {
        SyntaxKind[] kinds = SyntaxKind.values();
        List<SyntaxKind> unaryKinds = new ArrayList<>();
        for (SyntaxKind kind : kinds) {
            if (getUnaryOperatorPrecedence(kind) > 0)
                unaryKinds.add(kind);
        }
        return unaryKinds;
    }

    public static List<SyntaxKind> getBinaryOperatorKinds() {
        SyntaxKind[] kinds = SyntaxKind.values();
        List<SyntaxKind> binaryKinds = new ArrayList<>();
        for (SyntaxKind kind : kinds) {
            if (getBinaryOperatorPrecedence(kind) > 0)
                binaryKinds.add(kind);
        }
        return binaryKinds;
    }
}
