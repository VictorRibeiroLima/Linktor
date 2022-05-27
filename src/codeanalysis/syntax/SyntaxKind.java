package src.codeanalysis.syntax;

public enum SyntaxKind {

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

    LITERAL_EXPRESSION,
    BINARY_EXPRESSION,
    PARENTHESIZED_EXPRESSION,
    UNARY_EXPRESSION,
    TRUE_KEYWORD,
    EQUAL_EQUAL_TOKEN, EXCLAMATION_EQUAL_TOKEN, EXCLAMATION_TOKEN, AMPERSAND_AMPERSAND_TOKEN, PIPE_PIPE_TOKEN, FALSE_KEYWORD


}