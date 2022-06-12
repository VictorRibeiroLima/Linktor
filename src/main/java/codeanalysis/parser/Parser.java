package codeanalysis.parser;

import codeanalysis.diagnostics.DiagnosticBag;
import codeanalysis.lexer.Lexer;
import codeanalysis.source.SourceText;
import codeanalysis.syntax.*;
import codeanalysis.syntax.clause.ElseClauseSyntax;
import codeanalysis.syntax.clause.ForConditionClauseSyntax;
import codeanalysis.syntax.clause.ParameterClauseSyntax;
import codeanalysis.syntax.clause.TypeClauseSyntax;
import codeanalysis.syntax.expression.*;
import codeanalysis.syntax.member.FunctionMemberSyntax;
import codeanalysis.syntax.member.GlobalMemberSyntax;
import codeanalysis.syntax.member.MemberSyntax;
import codeanalysis.syntax.statements.*;

import java.util.ArrayList;
import java.util.List;

public final class Parser {

    private final List<SyntaxToken> tokens;
    private final SyntaxTree syntaxTree;

    private final SourceText text;
    private int position;


    private final DiagnosticBag diagnostics = new DiagnosticBag();

    public Parser(SyntaxTree syntaxTree) {

        position = 0;
        SyntaxToken token;
        List<SyntaxToken> tokens = new ArrayList<>();
        Lexer lexer = new Lexer(syntaxTree);
        do {
            token = lexer.lex();
            if (token.getKind() != SyntaxKind.WHITESPACE_TOKEN && token.getKind() != SyntaxKind.BAD_TOKEN)
                tokens.add(token);
        } while (token.getKind() != SyntaxKind.END_OF_FILE_TOKEN);
        this.tokens = tokens;
        this.syntaxTree = syntaxTree;
        this.text = syntaxTree.getText();
        diagnostics.addAll(lexer.getDiagnostics());
    }

    public DiagnosticBag getDiagnostics() {
        return diagnostics;
    }

    public CompilationUnitSyntax parseCompilationUnit() {
        List<MemberSyntax> members = parseMembers();
        SyntaxToken endOfFileToken = matchToken(SyntaxKind.END_OF_FILE_TOKEN);
        return new CompilationUnitSyntax(syntaxTree, members, endOfFileToken);
    }

    private List<MemberSyntax> parseMembers() {
        List<MemberSyntax> members = new ArrayList<>();
        while (getCurrent().getKind() != SyntaxKind.END_OF_FILE_TOKEN) {
            MemberSyntax member = parseMember();
            members.add(member);
        }
        return List.copyOf(members);
    }

    private MemberSyntax parseMember() {
        if (getCurrent().getKind() == SyntaxKind.FUNCTION_KEYWORD)
            return parseFunctionDeclaration();
        return parseGlobalStatement();

    }

    private MemberSyntax parseFunctionDeclaration() {
        SyntaxToken function = matchToken(SyntaxKind.FUNCTION_KEYWORD);
        SyntaxToken identifier = matchToken(SyntaxKind.IDENTIFIER_TOKEN);
        SyntaxToken open = matchToken(SyntaxKind.OPEN_PARENTHESIS_TOKEN);
        SeparatedSyntaxList<ParameterClauseSyntax> params = parseParameterList();
        SyntaxToken close = matchToken(SyntaxKind.CLOSE_PARENTHESIS_TOKEN);
        TypeClauseSyntax type = parseOptionalType();
        BlockStatementSyntax body = parseBlockStatement();
        return new FunctionMemberSyntax(syntaxTree, function, identifier, open, params, close, type, body);
    }

    private SeparatedSyntaxList<ParameterClauseSyntax> parseParameterList() {
        List<SyntaxNode> nodes = new ArrayList<>();
        while (getCurrent().getKind() != SyntaxKind.END_OF_FILE_TOKEN &&
                getCurrent().getKind() != SyntaxKind.CLOSE_PARENTHESIS_TOKEN) {
            ParameterClauseSyntax parameter = parseParameter();
            nodes.add(parameter);
            if (getCurrent().getKind() != SyntaxKind.CLOSE_PARENTHESIS_TOKEN) {
                SyntaxToken comma = matchToken(SyntaxKind.COMMA_TOKEN);
                nodes.add(comma);
            }
        }
        return new SeparatedSyntaxList<>(nodes);
    }

    private ParameterClauseSyntax parseParameter() {
        SyntaxToken identifier = matchToken(SyntaxKind.IDENTIFIER_TOKEN);
        TypeClauseSyntax type = parseType();
        return new ParameterClauseSyntax(syntaxTree, identifier, type);
    }

    private MemberSyntax parseGlobalStatement() {
        StatementSyntax statement = parseStatement();
        return new GlobalMemberSyntax(syntaxTree, statement);
    }


    private StatementSyntax parseStatement() {
        StatementSyntax statement = switch (getCurrent().getKind()) {
            case OPEN_BRACE_TOKEN -> parseBlockStatement();
            case VAR_KEYWORD, LET_KEYWORD -> parseVariableDeclarationStatement();
            case IF_KEYWORD -> parseIfStatement();
            case WHILE_KEYWORD -> parseWhileStatement();
            case FOR_KEYWORD -> parseForStatement();
            case BREAK_KEYWORD -> parseBreakStatement();
            case CONTINUE_KEYWORD -> parseContinueStatement();
            case RETURN_KEYWORD -> parseReturnStatement();
            default -> parseExpressionStatement();
        };
        if (getCurrent().getKind() == SyntaxKind.SEMICOLON_TOKEN)
            nextToken();
        return statement;
    }

    private StatementSyntax parseReturnStatement() {
        var keyword = matchToken(SyntaxKind.RETURN_KEYWORD);
        var keywordLine = text.getLineIndex(keyword.getSpan().start());
        var currentLine = text.getLineIndex(getCurrent().getSpan().start());
        var sameLine = keywordLine == currentLine;
        var isEoF = getCurrent().getKind() == SyntaxKind.END_OF_FILE_TOKEN;
        var needsExpression = !isEoF && sameLine && getCurrent().getKind() != SyntaxKind.SEMICOLON_TOKEN;
        var expression = needsExpression ? parseExpression() : null;
        return new ReturnStatementSyntax(syntaxTree, keyword, expression);
    }

    private StatementSyntax parseForStatement() {
        SyntaxToken forKeyWord = matchToken(SyntaxKind.FOR_KEYWORD);
        ForConditionClauseSyntax condition = parseForConditionClause();
        StatementSyntax thenStatement = parseStatement();
        return new ForStatementSyntax(syntaxTree, forKeyWord, condition, thenStatement);
    }

    private ForConditionClauseSyntax parseForConditionClause() {
        matchToken(SyntaxKind.OPEN_PARENTHESIS_TOKEN);
        SyntaxNode variableExpression = switch (getCurrent().getKind()) {
            case VAR_KEYWORD, LET_KEYWORD -> parseVariableDeclarationStatement();
            default -> parseNameExpression();
        };
        matchToken(SyntaxKind.SEMICOLON_TOKEN);
        ExpressionSyntax condition = parseExpression();
        matchToken(SyntaxKind.SEMICOLON_TOKEN);
        ExpressionSyntax increment = parseExpression();
        matchToken(SyntaxKind.CLOSE_PARENTHESIS_TOKEN);
        return new ForConditionClauseSyntax(syntaxTree, variableExpression, condition, increment);
    }

    private StatementSyntax parseWhileStatement() {
        SyntaxToken whileKeyword = matchToken(SyntaxKind.WHILE_KEYWORD);
        ExpressionSyntax condition = parseParenthesizedExpression();
        StatementSyntax thenStatement = parseStatement();
        return new WhileStatementSyntax(syntaxTree, whileKeyword, condition, thenStatement);
    }

    private StatementSyntax parseIfStatement() {
        SyntaxToken ifKeyword = matchToken(SyntaxKind.IF_KEYWORD);
        ExpressionSyntax condition = parseParenthesizedExpression();
        StatementSyntax thenStatement = parseStatement();
        ElseClauseSyntax elseClause = parseElseClause();
        return new IfStatementSyntax(syntaxTree, ifKeyword, condition, thenStatement, elseClause);
    }

    private ElseClauseSyntax parseElseClause() {
        if (getCurrent().getKind() == SyntaxKind.ELSE_KEYWORD) {
            SyntaxToken elseKeyword = matchToken(SyntaxKind.ELSE_KEYWORD);
            StatementSyntax thenStatement = parseStatement();
            return new ElseClauseSyntax(syntaxTree, elseKeyword, thenStatement);
        }
        return null;
    }

    private StatementSyntax parseVariableDeclarationStatement() {
        SyntaxToken keyword = matchToken(getCurrent().getKind());
        SyntaxToken identifier = matchToken(SyntaxKind.IDENTIFIER_TOKEN);
        TypeClauseSyntax type = parseOptionalType();
        SyntaxToken equals = matchToken(SyntaxKind.EQUAL_TOKEN);
        ExpressionSyntax initializer = parseExpression();
        return new VariableDeclarationStatementSyntax(syntaxTree, keyword, identifier, type, equals, initializer);

    }

    private StatementSyntax parseBreakStatement() {
        SyntaxToken breakKeyword = matchToken(SyntaxKind.BREAK_KEYWORD);
        return new BreakStatementSyntax(syntaxTree, breakKeyword);
    }

    private StatementSyntax parseContinueStatement() {
        SyntaxToken continueKeyword = matchToken(SyntaxKind.CONTINUE_KEYWORD);
        return new ContinueStatementSyntax(syntaxTree, continueKeyword);
    }

    private TypeClauseSyntax parseOptionalType() {
        if (getCurrent().getKind() != SyntaxKind.COLON_TOKEN)
            return null;
        return parseType();
    }

    private TypeClauseSyntax parseType() {
        SyntaxToken colon = matchToken(SyntaxKind.COLON_TOKEN);
        SyntaxToken identifier = matchToken(SyntaxKind.IDENTIFIER_TOKEN);
        return new TypeClauseSyntax(syntaxTree, colon, identifier);
    }

    private BlockStatementSyntax parseBlockStatement() {
        SyntaxToken open = matchToken(SyntaxKind.OPEN_BRACE_TOKEN);
        List<StatementSyntax> statements = new ArrayList<>();
        while (getCurrent().getKind() != SyntaxKind.END_OF_FILE_TOKEN &&
                getCurrent().getKind() != SyntaxKind.CLOSE_BRACE_TOKEN) {
            StatementSyntax statement = parseStatement();
            statements.add(statement);
        }
        SyntaxToken close = matchToken(SyntaxKind.CLOSE_BRACE_TOKEN);
        return new BlockStatementSyntax(syntaxTree, open, statements, close);
    }

    private StatementSyntax parseExpressionStatement() {
        ExpressionSyntax expression = parseExpression();
        return new ExpressionStatementSyntax(syntaxTree, expression);
    }

    private ExpressionSyntax parseExpression() {
        return parseAssignmentExpression();
    }

    private ExpressionSyntax parseAssignmentExpression() {
        if (getCurrent().getKind() == SyntaxKind.IDENTIFIER_TOKEN) {
            switch (peek(1).getKind()) {
                case PLUS_EQUALS_TOKEN, MINUS_EQUALS_TOKEN, SLASH_EQUALS_TOKEN, STAR_EQUALS_TOKEN,
                        AMPERSAND_EQUALS_TOKEN, PIPE_EQUALS_TOKEN, HAT_EQUALS_TOKEN, EQUAL_TOKEN -> {
                    var identifier = matchToken(SyntaxKind.IDENTIFIER_TOKEN);
                    var operator = matchToken(getCurrent().getKind());
                    var right = parseAssignmentExpression();
                    return new AssignmentExpressionSyntax(syntaxTree, identifier, operator, right);
                }
            }
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
            left = new BinaryExpressionSyntax(syntaxTree, left, operatorToken, right);

        }
        return left;
    }

    private ExpressionSyntax parseUnaryExpression() {
        int unaryPrecedence = SyntaxFacts.getUnaryOperatorPrecedence(getCurrent().getKind());
        if (unaryPrecedence > 0) {
            SyntaxToken operator = nextToken();
            ExpressionSyntax left = parseUnaryExpression();
            return new UnaryExpressionSyntax(syntaxTree, operator, left);
        }
        return parsePrimaryExpression();
    }

    private ExpressionSyntax parsePrimaryExpression() {
        return switch (getCurrent().getKind()) {
            case OPEN_PARENTHESIS_TOKEN -> parseParenthesizedExpression();
            case TRUE_KEYWORD, FALSE_KEYWORD -> parseBooleanLiteralExpression();
            case NUMBER_TOKEN -> parseNumberLiteralExpression();
            case STRING_TOKEN -> parseStringLiteralExpression();
            case PLUS_PLUS_TOKEN, MINUS_MINUS_TOKEN -> parsePrefixExpression();
            default -> parseIdentifierToken();
        };

    }

    private ExpressionSyntax parsePrefixExpression() {
        var token = matchToken(getCurrent().getKind());
        var identifier = matchToken(SyntaxKind.IDENTIFIER_TOKEN);
        return new PrefixExpressionSyntax(syntaxTree, token, identifier);
    }

    private ParenthesizedExpressionSyntax parseParenthesizedExpression() {
        SyntaxToken left = matchToken(SyntaxKind.OPEN_PARENTHESIS_TOKEN);
        ExpressionSyntax expression = parseExpression();
        SyntaxToken right = matchToken(SyntaxKind.CLOSE_PARENTHESIS_TOKEN);
        return new ParenthesizedExpressionSyntax(syntaxTree, left, expression, right);
    }

    private LiteralExpressionSyntax parseBooleanLiteralExpression() {
        boolean isTrue = getCurrent().getKind() == SyntaxKind.TRUE_KEYWORD;
        SyntaxToken token = isTrue ? matchToken(SyntaxKind.TRUE_KEYWORD) :
                matchToken(SyntaxKind.FALSE_KEYWORD);
        return new LiteralExpressionSyntax(syntaxTree, token, isTrue);
    }

    private LiteralExpressionSyntax parseStringLiteralExpression() {
        SyntaxToken stringToken = matchToken(SyntaxKind.STRING_TOKEN);
        return new LiteralExpressionSyntax(syntaxTree, stringToken);
    }

    private ExpressionSyntax parseIdentifierToken() {
        if (getCurrent().getKind() == SyntaxKind.IDENTIFIER_TOKEN && peek(1).getKind() == SyntaxKind.OPEN_PARENTHESIS_TOKEN)
            return parseCallExpression();
        else if (
                getCurrent().getKind() == SyntaxKind.IDENTIFIER_TOKEN
                        && (peek(1).getKind() == SyntaxKind.PLUS_PLUS_TOKEN
                        || peek(1).getKind() == SyntaxKind.MINUS_MINUS_TOKEN)
        )
            return parseSuffixExpression();
        return parseNameExpression();
    }

    private ExpressionSyntax parseSuffixExpression() {
        var identifier = matchToken(SyntaxKind.IDENTIFIER_TOKEN);
        var token = matchToken(getCurrent().getKind());
        return new SuffixExpressionSyntax(syntaxTree, identifier, token);
    }

    private ExpressionSyntax parseCallExpression() {
        SyntaxToken identifier = matchToken(SyntaxKind.IDENTIFIER_TOKEN);
        SyntaxToken open = matchToken(SyntaxKind.OPEN_PARENTHESIS_TOKEN);
        SeparatedSyntaxList<ExpressionSyntax> args = parseArguments();
        SyntaxToken close = matchToken(SyntaxKind.CLOSE_PARENTHESIS_TOKEN);
        return new CallExpressionSyntax(syntaxTree, identifier, open, args, close);
    }

    private SeparatedSyntaxList<ExpressionSyntax> parseArguments() {
        List<SyntaxNode> nodes = new ArrayList<>();
        while (getCurrent().getKind() != SyntaxKind.END_OF_FILE_TOKEN &&
                getCurrent().getKind() != SyntaxKind.CLOSE_PARENTHESIS_TOKEN) {
            ExpressionSyntax expression = parseExpression();
            nodes.add(expression);
            if (getCurrent().getKind() != SyntaxKind.CLOSE_PARENTHESIS_TOKEN) {
                SyntaxToken comma = matchToken(SyntaxKind.COMMA_TOKEN);
                nodes.add(comma);
            }
        }
        return new SeparatedSyntaxList<>(nodes);
    }

    private NameExpressionSyntax parseNameExpression() {
        SyntaxToken token = matchToken(SyntaxKind.IDENTIFIER_TOKEN);
        return new NameExpressionSyntax(syntaxTree, token);
    }

    private LiteralExpressionSyntax parseNumberLiteralExpression() {
        SyntaxToken token = matchToken(SyntaxKind.NUMBER_TOKEN);
        return new LiteralExpressionSyntax(syntaxTree, token);
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

        diagnostics.reportUnexpectedToken(getCurrent().getLocation(), getCurrent().getKind(), type);
        nextToken();
        return new SyntaxToken(syntaxTree, type, getCurrent().getPosition(), null, null);
    }
}
