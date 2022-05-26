package codeanalysis.syntax;

public final class SyntaxFacts {
    private SyntaxFacts() {

    }

    public static int getBinaryOperatorPrecedence(SyntaxType type) {
        switch (type) {
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
