package codeanalysis.parser;

import codeanalysis.lexer.Lexer;
import codeanalysis.syntax.SyntaxToken;
import codeanalysis.syntax.SyntaxTree;
import codeanalysis.syntax.SyntaxType;
import codeanalysis.syntax.expression.BinaryExpressionSyntax;
import codeanalysis.syntax.expression.ExpressionSyntax;
import codeanalysis.syntax.expression.LiteralExpressionSyntax;
import codeanalysis.syntax.expression.ParenthesizedExpressionSyntax;

import java.util.ArrayList;
import java.util.List;

public final class Parser {

    private final List<SyntaxToken> tokens;
    private int position;

    private final List<String> diagnostics = new ArrayList<>();

    public Parser(String text) {
        position = 0;
        SyntaxToken token;
        List<SyntaxToken> tokens = new ArrayList<>();
        Lexer lexer = new Lexer(text);
        do {
            token = lexer.lex();
            if (token.getType() != SyntaxType.WHITESPACE_TOKEN && token.getType() != SyntaxType.BAD_TOKEN)
                tokens.add(token);
            else if (token.getType() == SyntaxType.BAD_TOKEN) {
                diagnostics.add("ERROR: Bad Token input: " + token.getText());
            }
        } while (token.getType() != SyntaxType.END_OF_FILE_TOKEN);
        this.diagnostics.addAll(lexer.getDiagnostics());
        this.tokens = tokens;
    }

    public List<String> getDiagnostics() {
        return diagnostics;
    }


    public SyntaxTree parse() {
        ExpressionSyntax expression = parseExpression();
        SyntaxToken endOfFileToken = matchToken(SyntaxType.END_OF_FILE_TOKEN);
        return new SyntaxTree(expression, endOfFileToken, diagnostics);
    }

    private ExpressionSyntax parseExpression() {
        return parseTerm();
    }

    private ExpressionSyntax parseTerm() {
        ExpressionSyntax left = parseFactor();
        while (
                getCurrent().getType() == SyntaxType.PLUS_TOKEN ||
                        getCurrent().getType() == SyntaxType.MINUS_TOKEN
        ) {
            SyntaxToken operatorToken = nextToken();
            ExpressionSyntax right = parseFactor();
            left = new BinaryExpressionSyntax(left, operatorToken, right);
        }
        return left;
    }

    private ExpressionSyntax parseFactor() {
        ExpressionSyntax left = parsePrimaryExpression();
        while (getCurrent().getType() == SyntaxType.MULTIPLICATION_TOKEN ||
                getCurrent().getType() == SyntaxType.DIVISION_TOKEN) {
            SyntaxToken operatorToken = nextToken();
            ExpressionSyntax right = parsePrimaryExpression();
            left = new BinaryExpressionSyntax(left, operatorToken, right);
        }
        return left;
    }

    private SyntaxToken peek(int offset) {
        int index = position - offset;
        if (index >= tokens.size())
            return tokens.get(position - 1);
        return tokens.get(index);
    }

    private SyntaxToken getCurrent() {
        return peek(0);
    }

    private SyntaxToken nextToken() {
        SyntaxToken token = getCurrent();
        position++;
        return token;
    }

    private SyntaxToken matchToken(SyntaxType type) {
        if (getCurrent().getType() == type)
            return nextToken();

        diagnostics.add("ERROR: unexpected token '" + getCurrent().getType() + "' expected '" + type + "'");
        return new SyntaxToken(type, getCurrent().getPosition(), null, null);
    }

    private ExpressionSyntax parsePrimaryExpression() {
        if (getCurrent().getType() == SyntaxType.OPEN_PARENTHESIS_TOKEN) {
            SyntaxToken left = nextToken();
            ExpressionSyntax expression = parseExpression();
            SyntaxToken right = matchToken(SyntaxType.CLOSE_PARENTHESIS_TOKEN);
            return new ParenthesizedExpressionSyntax(left, expression, right);

        }
        SyntaxToken token = matchToken(SyntaxType.NUMBER_TOKEN);
        return new LiteralExpressionSyntax(token);
    }
}
