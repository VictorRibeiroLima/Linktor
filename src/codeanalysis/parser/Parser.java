package src.codeanalysis.parser;

import src.codeanalysis.diagnostics.DiagnosticBag;
import src.codeanalysis.lexer.Lexer;
import src.codeanalysis.syntax.SyntaxFacts;
import src.codeanalysis.syntax.SyntaxKind;
import src.codeanalysis.syntax.SyntaxToken;
import src.codeanalysis.syntax.SyntaxTree;
import src.codeanalysis.syntax.expression.*;

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

    public DiagnosticBag getDiagnostics() {
        return diagnostics;
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
        ExpressionSyntax left = parseUnaryExpression(parentPrecedence);
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

    private ExpressionSyntax parseUnaryExpression(int parentPrecedence) {
        int unaryPrecedence = SyntaxFacts.getUnaryOperatorPrecedence(getCurrent().getKind());
        if (unaryPrecedence > parentPrecedence) {
            SyntaxToken operator = nextToken();
            ExpressionSyntax left = parseBinaryExpression(0);
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
