package codeanalysis.parser;

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

    private final List<String> diagnostics = new ArrayList<>();

    public Parser(String text) {
        position = 0;
        SyntaxToken token;
        List<SyntaxToken> tokens = new ArrayList<>();
        Lexer lexer = new Lexer(text);
        do {
            token = lexer.lex();
            if (token.getKind() != SyntaxKind.WHITESPACE_TOKEN && token.getKind() != SyntaxKind.BAD_TOKEN)
                tokens.add(token);
            else if (token.getKind() == SyntaxKind.BAD_TOKEN) {
                diagnostics.add("ERROR: Bad Token input: " + token.getText());
            }
        } while (token.getKind() != SyntaxKind.END_OF_FILE_TOKEN);
        this.diagnostics.addAll(lexer.getDiagnostics());
        this.tokens = tokens;
    }

    public List<String> getDiagnostics() {
        return diagnostics;
    }


    public SyntaxTree parse() {
        ExpressionSyntax expression = parseExpression();
        SyntaxToken endOfFileToken = matchToken(SyntaxKind.END_OF_FILE_TOKEN);
        return new SyntaxTree(expression, endOfFileToken, diagnostics);
    }

    private ExpressionSyntax parseExpression() {
        return parseExpression(0);
    }


    private ExpressionSyntax parseExpression(int parentPrecedence) {
        ExpressionSyntax left;
        int unaryPrecedence = SyntaxFacts.getUnaryOperatorPrecedence(getCurrent().getKind());
        if (unaryPrecedence > parentPrecedence) {
            SyntaxToken operator = nextToken();
            left = parseExpression(0);
            return new UnaryExpressionSyntax(operator, left);
        } else {
            left = parsePrimaryExpression();
        }
        while (true) {
            int precedence = SyntaxFacts.getBinaryOperatorPrecedence(getCurrent().getKind());
            if (precedence <= parentPrecedence)
                break;

            SyntaxToken operatorToken = nextToken();
            ExpressionSyntax right = parseExpression(precedence);
            left = new BinaryExpressionSyntax(left, operatorToken, right);
        }
        return left;
    }

    private ExpressionSyntax parsePrimaryExpression() {
        if (getCurrent().getKind() == SyntaxKind.OPEN_PARENTHESIS_TOKEN) {
            SyntaxToken left = nextToken();
            ExpressionSyntax expression = parseExpression();
            SyntaxToken right = matchToken(SyntaxKind.CLOSE_PARENTHESIS_TOKEN);
            return new ParenthesizedExpressionSyntax(left, expression, right);

        }
        SyntaxToken token = matchToken(SyntaxKind.NUMBER_TOKEN);
        return new LiteralExpressionSyntax(token);
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

    private SyntaxToken matchToken(SyntaxKind type) {
        if (getCurrent().getKind() == type)
            return nextToken();

        diagnostics.add("ERROR: unexpected token '" + getCurrent().getKind() + "' expected '" + type + "'");
        return new SyntaxToken(type, getCurrent().getPosition(), null, null);
    }
}
