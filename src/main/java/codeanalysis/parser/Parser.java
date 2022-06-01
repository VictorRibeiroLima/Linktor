package codeanalysis.parser;

import codeanalysis.diagnostics.DiagnosticBag;
import codeanalysis.diagnostics.text.SourceText;
import codeanalysis.lexer.Lexer;
import codeanalysis.syntax.CompilationUnitSyntax;
import codeanalysis.syntax.SyntaxFacts;
import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxToken;
import codeanalysis.syntax.expression.*;
import codeanalysis.syntax.statements.*;

import java.util.ArrayList;
import java.util.List;

public final class Parser {

    private final List<SyntaxToken> tokens;
    private int position;

    private final DiagnosticBag diagnostics = new DiagnosticBag();

    private final SourceText text;

    public Parser(SourceText text) {
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
        this.text = text;
    }

    public DiagnosticBag getDiagnostics() {
        return diagnostics;
    }

    public CompilationUnitSyntax parseCompilationUnit() {
        StatementSyntax expression = parseStatement();
        SyntaxToken endOfFileToken = matchToken(SyntaxKind.END_OF_FILE_TOKEN);
        return new CompilationUnitSyntax(expression, endOfFileToken);
    }


    private StatementSyntax parseStatement() {
        return switch (getCurrent().getKind()) {
            case OPEN_BRACE_TOKEN -> parseBlockStatement();
            case VAR_KEYWORD, LET_KEYWORD -> parseVariableDeclarationStatement();
            case IF_KEYWORD -> parseIfStatement();
            default -> parseExpressionStatement();
        };
    }

    private StatementSyntax parseIfStatement() {
        SyntaxToken ifKeyword = matchToken(SyntaxKind.IF_KEYWORD);
        ExpressionSyntax condition = parseParenthesizedExpression();
        StatementSyntax thenStatement = parseStatement();
        ElseClauseSyntax elseClause = parseElseClause();
        return new IfStatementSyntax(ifKeyword, condition, thenStatement, elseClause);
    }

    private ElseClauseSyntax parseElseClause() {
        if (getCurrent().getKind() == SyntaxKind.ELSE_KEYWORD) {
            SyntaxToken elseKeyword = matchToken(SyntaxKind.ELSE_KEYWORD);
            StatementSyntax thenStatement = parseStatement();
            return new ElseClauseSyntax(elseKeyword, thenStatement);
        }
        return null;
    }

    private StatementSyntax parseVariableDeclarationStatement() {
        SyntaxToken keyword = matchToken(getCurrent().getKind());
        SyntaxToken identifier = matchToken(SyntaxKind.IDENTIFIER_TOKEN);
        SyntaxToken equals = matchToken(SyntaxKind.EQUAL_TOKEN);
        ExpressionSyntax initializer = parseExpression();
        return new VariableDeclarationStatementSyntax(keyword, identifier, equals, initializer);

    }

    private StatementSyntax parseBlockStatement() {
        SyntaxToken open = matchToken(SyntaxKind.OPEN_BRACE_TOKEN);
        List<StatementSyntax> statements = new ArrayList<>();
        while (getCurrent().getKind() != SyntaxKind.END_OF_FILE_TOKEN &&
                getCurrent().getKind() != SyntaxKind.CLOSE_BRACE_TOKEN) {
            StatementSyntax statement = parseStatement();
            statements.add(statement);
        }
        SyntaxToken close = matchToken(SyntaxKind.CLOSE_BRACE_TOKEN);
        return new BlockStatementSyntax(open, statements, close);
    }

    private StatementSyntax parseExpressionStatement() {
        ExpressionSyntax expression = parseExpression();
        return new ExpressionStatementSyntax(expression);
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
            ExpressionSyntax left = parseUnaryExpression();
            return new UnaryExpressionSyntax(operator, left);
        }
        return parsePrimaryExpression();
    }

    private ExpressionSyntax parsePrimaryExpression() {
        return switch (getCurrent().getKind()) {
            case OPEN_PARENTHESIS_TOKEN -> parseParenthesizedExpression();
            case TRUE_KEYWORD, FALSE_KEYWORD -> parseBooleanLiteralExpression();
            case NUMBER_TOKEN -> parseNumberLiteralExpression();
            default -> parseNameExpression();
        };

    }

    private ParenthesizedExpressionSyntax parseParenthesizedExpression() {
        SyntaxToken left = matchToken(SyntaxKind.OPEN_PARENTHESIS_TOKEN);
        ExpressionSyntax expression = parseExpression();
        SyntaxToken right = matchToken(SyntaxKind.CLOSE_PARENTHESIS_TOKEN);
        return new ParenthesizedExpressionSyntax(left, expression, right);
    }

    private LiteralExpressionSyntax parseBooleanLiteralExpression() {
        boolean isTrue = getCurrent().getKind() == SyntaxKind.TRUE_KEYWORD;
        SyntaxToken token = isTrue ? matchToken(SyntaxKind.TRUE_KEYWORD) :
                matchToken(SyntaxKind.FALSE_KEYWORD);
        return new LiteralExpressionSyntax(token, isTrue);
    }

    private NameExpressionSyntax parseNameExpression() {
        SyntaxToken token = matchToken(SyntaxKind.IDENTIFIER_TOKEN);
        return new NameExpressionSyntax(token);
    }

    private LiteralExpressionSyntax parseNumberLiteralExpression() {
        SyntaxToken token = matchToken(SyntaxKind.NUMBER_TOKEN);
        return new LiteralExpressionSyntax(token);
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
        nextToken();
        return new SyntaxToken(type, getCurrent().getPosition(), null, null);
    }
}
