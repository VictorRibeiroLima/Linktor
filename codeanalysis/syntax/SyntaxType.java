package codeanalysis.syntax;

public enum SyntaxType {

    BAD_TOKEN,
    WHITESPACE_TOKEN,
    END_OF_FILE_TOKEN,
    PLUS_TOKEN,
    NUMBER_TOKEN,
    MINUS_TOKEN,
    DIVISION_TOKEN,
    MULTIPLICATION_TOKEN,
    OPEN_PARENTHESIS_TOKEN,
    CLOSE_PARENTHESIS_TOKEN,
    EQUAL_TOKEN,
    LITERAL_EXPRESSION,
    BINARY_EXPRESSION,
    PARENTHESIZED_EXPRESSION,
    UNARY_EXPRESSION;

    public static SyntaxType get(char representation) {
        if (Character.isDigit(representation)) {
            return NUMBER_TOKEN;
        } else if (representation == '+')
            return PLUS_TOKEN;
        else if (representation == '-')
            return MINUS_TOKEN;
        else if (representation == '/') {
            return DIVISION_TOKEN;
        } else if (representation == '*') {
            return MULTIPLICATION_TOKEN;
        } else if (representation == '(') {
            return OPEN_PARENTHESIS_TOKEN;
        } else if (representation == ')') {
            return CLOSE_PARENTHESIS_TOKEN;
        } else if (representation == '=') {
            return EQUAL_TOKEN;
        }
        return BAD_TOKEN;
    }

    public static SyntaxType get(String representation) {
        if (representation.matches("-?\\d+"))
            return NUMBER_TOKEN;
        else if (representation.isBlank()) {
            return WHITESPACE_TOKEN;
        }
        return BAD_TOKEN;
    }
}
