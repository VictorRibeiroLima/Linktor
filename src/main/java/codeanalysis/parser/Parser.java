package codeanalysis.parser;

import codeanalysis.diagnostics.DiagnosticBag;
import codeanalysis.lexer.Lexer;
import codeanalysis.syntax.SyntaxFacts;
import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxToken;
import codeanalysis.syntax.SyntaxTree;
import codeanalysis.syntax.expression.*;

import java.util.ArrayList;
import java.util.List;

public final class Parser {

    private final List<SyntaxToken> tokens;
    private int position;

    private final DiagnosticBag diagnostics = new DiagnosticBag();

    public Parser(String text) {
        position = 0;
        SyntaxToken token;
        List<SyntaxToken> tokens = new ArrayList<>();
        Lexer lexer = new Lexer(text);
        do {
            token = lexer.lex();
            if (token.getKind() != SyntaxKind.WHITESPACE_TOKEN && token.getKind() != SyntaxKind.BAD_TOKEN)
                tokens.add(token);
        } while (token.getKind() != SyntaxKind.END_OF_FILE_TOKEN);
        this.tokens = tokens;
        diagnostics.addAll(lexer.getDiagnostics());
    }

    public SyntaxTree parse() {
        ExpressionSyntax expression = parseExpression();
        SyntaxToken endOfFileToken = matchToken(SyntaxKind.END_OF_FILE_TOKEN);
        return new SyntaxTree(expression, endOfFileToken, diagnostics);
    }

    private ExpressionSyntax parseExpression() {
        return parseAssignmentExpression();
    }

    private ExpressionSyntax parseAssignmentExpression() {
        if (getCurrent().getKind() == SyntaxKind.IDENTIFIER_TOKEN &&
                peek(1).getKind() == SyntaxKind.EQUAL_TOKEN) {
            SyntaxToken identifier = nextToken();
            SyntaxToken equals = nextToken();
            ExpressionSyntax right = parseAssignmentExpression();
            return new AssignmentExpressionSyntax(identifier, equals, right);
        }
        return parseBinaryExpression();
    }

    private ExpressionSyntax parseBinaryExpression() {
        return parseBinaryExpression(0);
    }


    private ExpressionSyntax parseBinaryExpression(int parentPrecedence) {
        ExpressionSyntax left = parseUnaryExpression();
        while (true) {
            int precedence = SyntaxFacts.getBinaryOperatorPrecedence(getCurrent().getKind());
            if (precedence <= parentPrecedence)
                break;

            SyntaxToken operatorToken = nextToken();
            ExpressionSyntax right = parseBinaryExpression(precedence);
            left = new BinaryExpressionSyntax(left, operatorToken, right);

        }
        return left;
    }

    private ExpressionSyntax parseUnaryExpression() {
        int unaryPrecedence = SyntaxFacts.getUnaryOperatorPrecedence(getCurrent().getKind());
        if (unaryPrecedence > 0) {
            SyntaxToken operator = nextToken();
            ExpressionSyntax left = parsePrimaryExpression();
            return new UnaryExpressionSyntax(operator, left);
        }
        return parsePrimaryExpression();
    }

    private ExpressionSyntax parsePrimaryExpression() {
        switch (getCurrent().getKind()) {
            case OPEN_PARENTHESIS_TOKEN: {
                SyntaxToken left = nextToken();
                ExpressionSyntax expression = parseExpression();
                SyntaxToken right = matchToken(SyntaxKind.CLOSE_PARENTHESIS_TOKEN);
                return new ParenthesizedExpressionSyntax(left, expression, right);
            }
            case TRUE_KEYWORD:
            case FALSE_KEYWORD: {
                SyntaxToken token = nextToken();
                boolean value = token.getKind() == SyntaxKind.TRUE_KEYWORD;
                return new LiteralExpressionSyntax(token, value);
            }
            case IDENTIFIER_TOKEN:
                return new NameExpressionSyntax(nextToken());
            default: {
                SyntaxToken token = matchToken(SyntaxKind.NUMBER_TOKEN);
                return new LiteralExpressionSyntax(token);
            }
        }

    }

    private SyntaxToken peek(int offset) {
        int index = position + offset;
        if (index >= tokens.size())
            return peek(offset - 1);
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

    private SyntaxToken matchToken(SyntaxKind type) {
        if (getCurrent().getKind() == type)
            return nextToken();

        diagnostics.reportUnexpectedToken(getCurrent().getSpan(), getCurrent().getKind(), type);
        return new SyntaxToken(type, getCurrent().getPosition(), null, null);
    }
}
