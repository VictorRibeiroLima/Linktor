package codeanalysis.syntax;

public final class SyntaxFacts {
    private SyntaxFacts() {

    }

    public static int getUnaryOperatorPrecedence(SyntaxKind kind) {
        switch (kind) {
            case PLUS_TOKEN:
            case MINUS_TOKEN:
                return 3;
            default:
                return 0;
        }
    }

    public static int getBinaryOperatorPrecedence(SyntaxKind kind) {
        switch (kind) {
            case MULTIPLICATION_TOKEN:
            case DIVISION_TOKEN:
                return 2;
            case PLUS_TOKEN:
            case MINUS_TOKEN:
                return 1;
            default:
                return 0;
        }
    }
}
