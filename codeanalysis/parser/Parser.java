package codeanalysis.parser;

import codeanalysis.lexer.Lexer;
import codeanalysis.syntax.SyntaxToken;
import codeanalysis.syntax.SyntaxTree;
import codeanalysis.syntax.SyntaxType;
import codeanalysis.syntax.expression.BinaryExpressionSyntax;
import codeanalysis.syntax.expression.ExpressionSyntax;
import codeanalysis.syntax.expression.NumberExpressionSyntax;
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
            token = lexer.nextToken();
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
        ExpressionSyntax expression = parseTerm();
        SyntaxToken endOfFileToken = matchToken(SyntaxType.END_OF_FILE_TOKEN);
        return new SyntaxTree(expression, endOfFileToken, diagnostics);
    }

    private ExpressionSyntax parseExpression() {
        return parseTerm();
    }

    private ExpressionSyntax parseTerm() {
        ExpressionSyntax left = parseFactor();
        while (
                current().getType() == SyntaxType.PLUS_TOKEN ||
                        current().getType() == SyntaxType.MINUS_TOKEN
        ) {
            SyntaxToken operatorToken = nextToken();
            ExpressionSyntax right = parseFactor();
            left = new BinaryExpressionSyntax(left, operatorToken, right);
        }
        return left;
    }

    private ExpressionSyntax parseFactor() {
        ExpressionSyntax left = parsePrimaryExpression();
        while (current().getType() == SyntaxType.MULTIPLICATION_TOKEN ||
                current().getType() == SyntaxType.DIVISION_TOKEN) {
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

    private SyntaxToken current() {
        return peek(0);
    }

    private SyntaxToken nextToken() {
        SyntaxToken token = current();
        position++;
        return token;
    }

    private SyntaxToken matchToken(SyntaxType type) {
        if (current().getType() == type)
            return nextToken();

        diagnostics.add("ERROR: unexpected token '" + current().getType() + "' expected '" + type + "'");
        return new SyntaxToken(type, current().getPosition(), null, null);
    }

    private ExpressionSyntax parsePrimaryExpression() {
        if (current().getType() == SyntaxType.OPEN_PARENTHESIS_TOKEN) {
            SyntaxToken left = nextToken();
            ExpressionSyntax expression = parseExpression();
            SyntaxToken right = matchToken(SyntaxType.CLOSE_PARENTHESIS_TOKEN);
            return new ParenthesizedExpressionSyntax(left, expression, right);

        }
        SyntaxToken token = matchToken(SyntaxType.NUMBER_TOKEN);
        return new NumberExpressionSyntax(token);
    }
}
