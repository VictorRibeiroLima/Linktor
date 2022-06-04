package codeanalysis.syntax;

public enum SyntaxKind {
    //UNITS
    COMPILATION_UNIT,

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
    GREATER_TOKEN,
    GREATER_EQUAL_TOKEN,
    LESS_TOKEN,
    LESS_EQUAL_TOKEN,
    OPEN_BRACE_TOKEN,
    CLOSE_BRACE_TOKEN,
    TILDE_TOKEN,
    AMPERSAND_TOKEN,
    PIPE_TOKEN,
    HAT_TOKEN,
    SEMICOLON_TOKEN,
    STRING_TOKEN,

    //EXPRESSIONS
    LITERAL_EXPRESSION,
    BINARY_EXPRESSION,
    PARENTHESIZED_EXPRESSION,
    UNARY_EXPRESSION,
    NAME_EXPRESSION,
    ASSIGNMENT_EXPRESSION,

    //KEYWORDS
    TRUE_KEYWORD,
    FALSE_KEYWORD,
    LET_KEYWORD,
    VAR_KEYWORD,
    IF_KEYWORD,
    ELSE_KEYWORD,
    WHILE_KEYWORD,
    FOR_KEYWORD,

    //STATEMENTS
    EXPRESSION_STATEMENT,
    BLOCK_STATEMENT,
    VARIABLE_DECLARATION_STATEMENT,
    IF_STATEMENT,
    ElSE_CLAUSE,
    WHILE_STATEMENT,
    FOR_STATEMENT,
    FOR_CONDITION_CLAUSE,

}
