package codeanalysis.syntax;

import java.util.ArrayList;
import java.util.List;

public final class SyntaxFacts {
    private SyntaxFacts() {

    }

    public static int getUnaryOperatorPrecedence(SyntaxKind kind) {
        return switch (kind) {
            case PLUS_TOKEN, MINUS_TOKEN, EXCLAMATION_TOKEN, TILDE_TOKEN -> 6;
            default -> 0;
        };
    }

    public static int getBinaryOperatorPrecedence(SyntaxKind kind) {
        return switch (kind) {
            case STAR_TOKEN, SLASH_TOKEN -> 5;
            case PLUS_TOKEN, MINUS_TOKEN -> 4;
            case EQUAL_EQUAL_TOKEN, EXCLAMATION_EQUAL_TOKEN, LESS_TOKEN, LESS_EQUAL_TOKEN, GREATER_TOKEN, GREATER_EQUAL_TOKEN ->
                    3;
            case AMPERSAND_AMPERSAND_TOKEN, AMPERSAND_TOKEN -> 2;
            case PIPE_PIPE_TOKEN, PIPE_TOKEN, HAT_TOKEN -> 1;
            default -> 0;
        };
    }

    public static SyntaxKind getKeywordKind(String text) {
        return switch (text) {
            case "true" -> SyntaxKind.TRUE_KEYWORD;
            case "false" -> SyntaxKind.FALSE_KEYWORD;
            case "var" -> SyntaxKind.VAR_KEYWORD;
            case "let" -> SyntaxKind.LET_KEYWORD;
            case "if" -> SyntaxKind.IF_KEYWORD;
            case "else" -> SyntaxKind.ELSE_KEYWORD;
            case "while" -> SyntaxKind.WHILE_KEYWORD;
            case "for" -> SyntaxKind.FOR_KEYWORD;
            default -> SyntaxKind.IDENTIFIER_TOKEN;
        };
    }

    public static String getText(SyntaxKind kind) {
        return switch (kind) {
            case SEMICOLON_TOKEN -> ";";
            case PLUS_TOKEN -> "+";
            case MINUS_TOKEN -> "-";
            case SLASH_TOKEN -> "/";
            case STAR_TOKEN -> "*";
            case OPEN_PARENTHESIS_TOKEN -> "(";
            case CLOSE_PARENTHESIS_TOKEN -> ")";
            case OPEN_BRACE_TOKEN -> "{";
            case CLOSE_BRACE_TOKEN -> "}";
            case EQUAL_TOKEN -> "=";
            case EQUAL_EQUAL_TOKEN -> "==";
            case EXCLAMATION_EQUAL_TOKEN -> "!=";
            case GREATER_TOKEN -> ">";
            case GREATER_EQUAL_TOKEN -> ">=";
            case LESS_TOKEN -> "<";
            case LESS_EQUAL_TOKEN -> "<=";
            case EXCLAMATION_TOKEN -> "!";
            case AMPERSAND_AMPERSAND_TOKEN -> "&&";
            case AMPERSAND_TOKEN -> "&";
            case PIPE_PIPE_TOKEN -> "||";
            case PIPE_TOKEN -> "|";
            case HAT_TOKEN -> "^";
            case TILDE_TOKEN -> "~";
            case FALSE_KEYWORD -> "false";
            case TRUE_KEYWORD -> "true";
            case VAR_KEYWORD -> "var";
            case LET_KEYWORD -> "let";
            case IF_KEYWORD -> "if";
            case ELSE_KEYWORD -> "else";
            case WHILE_KEYWORD -> "while";
            case FOR_KEYWORD -> "for";
            default -> null;
        };
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
