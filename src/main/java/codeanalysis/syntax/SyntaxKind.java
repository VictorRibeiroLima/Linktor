package codeanalysis.syntax;

public enum SyntaxKind {
    //TOKENS
    BAD_TOKEN,
    WHITESPACE_TOKEN,
    END_OF_FILE_TOKEN,
    PLUS_TOKEN,
    NUMBER_TOKEN,
    MINUS_TOKEN,
    SLASH_TOKEN,
    STAR_TOKEN,
    OPEN_PARENTHESIS_TOKEN,
    CLOSE_PARENTHESIS_TOKEN,
    EQUAL_TOKEN,
    IDENTIFIER_TOKEN,
    EQUAL_EQUAL_TOKEN,
    EXCLAMATION_EQUAL_TOKEN,
    EXCLAMATION_TOKEN,
    AMPERSAND_AMPERSAND_TOKEN,
    PIPE_PIPE_TOKEN,

    //EXPRESSIONS
    LITERAL_EXPRESSION,
    BINARY_EXPRESSION,
    PARENTHESIZED_EXPRESSION,
    UNARY_EXPRESSION,
    NAME_EXPRESSION,
    ASSIGNMENT_EXPRESSION,

    //KEYWORDS
    TRUE_KEYWORD,
    FALSE_KEYWORD;

    public static String getText(SyntaxKind kind){
        switch (kind){
            case PLUS_TOKEN:
                return "+";
            case NUMBER_TOKEN:
                return "1";
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
            case EQUAL_TOKEN:
                return "=";
            case EQUAL_EQUAL_TOKEN:
                return "==";
            case EXCLAMATION_EQUAL_TOKEN:
                return "!=";
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
            default:
                return null;
        }
    }

}
