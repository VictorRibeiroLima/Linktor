package codeanalysis.binding;

import codeanalysis.binding.clause.BoundElseClause;
import codeanalysis.binding.clause.BoundForConditionClause;
import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.binding.expression.assignment.BoundAssignmentExpression;
import codeanalysis.binding.expression.binary.BoundBinaryExpression;
import codeanalysis.binding.expression.binary.BoundBinaryOperator;
import codeanalysis.binding.expression.literal.BoundLiteralExpression;
import codeanalysis.binding.expression.unary.BoundUnaryExpression;
import codeanalysis.binding.expression.unary.BoundUnaryOperator;
import codeanalysis.binding.expression.variable.BoundVariableExpression;
import codeanalysis.binding.scopes.BoundGlobalScope;
import codeanalysis.binding.scopes.BoundScope;
import codeanalysis.binding.statement.*;
import codeanalysis.diagnostics.Diagnostic;
import codeanalysis.diagnostics.DiagnosticBag;
import codeanalysis.symbol.VariableSymbol;
import codeanalysis.syntax.CompilationUnitSyntax;
import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.clause.ElseClauseSyntax;
import codeanalysis.syntax.clause.ForConditionClause;
import codeanalysis.syntax.expression.*;
import codeanalysis.syntax.statements.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Binder {

    private final DiagnosticBag diagnostics = new DiagnosticBag();

    private BoundScope scope;

    private Binder(BoundScope parent) {
        scope = new BoundScope(parent);
    }

    public DiagnosticBag getDiagnostics() {
        return diagnostics;
    }

    public static BoundGlobalScope boundGlobalScope(CompilationUnitSyntax unit, BoundGlobalScope previous) throws Exception {
        BoundScope parent = createParentScope(previous);
        Binder binder = new Binder(parent);
        BoundStatement expression = binder.bindStatement(unit.getExpression());
        List<VariableSymbol> variables = binder.scope.getDeclaredVariables();
        List<Diagnostic> diagnostics = binder.getDiagnostics().toUnmodifiableList();
        return new BoundGlobalScope(previous, diagnostics, variables, expression);
    }

    private static BoundScope createParentScope(BoundGlobalScope previous) {
        Stack<BoundGlobalScope> stack = new Stack<>();
        while (previous != null) {
            stack.add(previous);
            previous = previous.getPrevious();
        }

        BoundScope parentScope = null;

        while (!stack.empty()) {
            previous = stack.pop();
            BoundScope scope = new BoundScope(parentScope);
            for (VariableSymbol variable : previous.getVariables()) {
                scope.declareVariable(variable);
            }
            parentScope = scope;
        }
        return parentScope;
    }

    public BoundStatement bindStatement(StatementSyntax syntax) throws Exception {
        return switch (syntax.getKind()) {
            case BLOCK_STATEMENT -> bindBlockStatement((BlockStatementSyntax) syntax);
            case EXPRESSION_STATEMENT -> bindExpressionStatement((ExpressionStatementSyntax) syntax);
            case VARIABLE_DECLARATION_STATEMENT ->
                    bindVariableDeclarationStatement((VariableDeclarationStatementSyntax) syntax);
            case IF_STATEMENT -> bindIfStatement((IfStatementSyntax) syntax);
            case WHILE_STATEMENT -> bindWhileStatement((WhileStatementSyntax) syntax);
            case FOR_STATEMENT -> bindForStatement((ForStatementSyntax) syntax);
            default -> throw new Exception("ERROR: unexpected syntax: " + syntax.getKind());
        };
    }

    private BoundStatement bindForStatement(ForStatementSyntax syntax) throws Exception {
        BoundForConditionClause clause = bindForConditionClause(syntax.getCondition());
        BoundStatement thenStatement = bindStatement(syntax.getThenStatement());
        return new BoundForStatement(clause, thenStatement);
    }

    private BoundForConditionClause bindForConditionClause(ForConditionClause condition) throws Exception {
        BoundNode variable;
        this.scope = new BoundScope(scope);
        if (condition.getVariableNode().getKind() == SyntaxKind.VARIABLE_DECLARATION_STATEMENT)
            variable = bindVariableDeclarationStatement((VariableDeclarationStatementSyntax) condition.getVariableNode());
        else
            variable = bindNameExpression((NameExpressionSyntax) condition.getVariableNode());

        BoundExpression conditionExpression = bindExpression(condition.getConditionExpression(), Boolean.class);
        BoundExpression incrementExpression = bindExpression(condition.getIncrementExpression(), Integer.class);
        return new BoundForConditionClause(variable, conditionExpression, incrementExpression);
    }

    private BoundStatement bindWhileStatement(WhileStatementSyntax syntax) throws Exception {
        BoundExpression condition = bindExpression(syntax.getCondition(), Boolean.class);
        BoundStatement thenStatement = bindStatement(syntax.getThenStatement());
        return new BoundWhileStatement(condition, thenStatement);
    }

    private BoundStatement bindIfStatement(IfStatementSyntax syntax) throws Exception {
        BoundExpression condition = bindExpression(syntax.getCondition(), Boolean.class);
        BoundStatement thenStatement = bindStatement(syntax.getThenStatement());
        BoundElseClause elseClause = bindElseClause(syntax.getElseClause());
        return new BoundIfStatement(condition, thenStatement, elseClause);
    }

    private BoundElseClause bindElseClause(ElseClauseSyntax elseClause) throws Exception {
        if (elseClause != null) {
            BoundStatement thenStatement = bindStatement(elseClause.getThenStatement());
            return new BoundElseClause(thenStatement);
        }
        return null;
    }

    private BoundStatement bindVariableDeclarationStatement(VariableDeclarationStatementSyntax syntax) throws Exception {
        boolean isReadOnly = syntax.getKeyword().getKind() == SyntaxKind.LET_KEYWORD;
        String name = syntax.getIdentifier().getText();
        BoundExpression initializer = bindExpression(syntax.getInitializer());
        VariableSymbol variableSymbol = new VariableSymbol(name, initializer.getType(), isReadOnly);
        if (!scope.declareVariable(variableSymbol))
            diagnostics.reportVariableAlreadyDeclared(name, syntax.getIdentifier().getSpan());

        return new BoundVariableDeclarationStatement(variableSymbol, initializer);
    }

    private BoundBlockStatement bindBlockStatement(BlockStatementSyntax syntax) throws Exception {
        List<BoundStatement> statements = new ArrayList<>();
        scope = new BoundScope(scope);
        for (StatementSyntax e : syntax.getStatements()
        ) {
            BoundStatement statement = bindStatement(e);
            statements.add(statement);
        }
        scope = scope.getParent();
        return new BoundBlockStatement(statements);
    }

    private BoundExpressionStatement bindExpressionStatement(ExpressionStatementSyntax syntax) throws Exception {
        BoundExpression expression = bindExpression(syntax.getExpression());
        return new BoundExpressionStatement(expression);
    }

    private BoundExpression bindExpression(ExpressionSyntax syntax, Type expectedType) throws Exception {
        BoundExpression result = bindExpression(syntax);
        if (!result.getType().equals(expectedType))
            diagnostics.reportCannotConvert(syntax.getSpan(), expectedType, result.getType());
        return result;
    }

    private BoundExpression bindExpression(ExpressionSyntax syntax) throws Exception {
        return switch (syntax.getKind()) {
            case PARENTHESIZED_EXPRESSION -> bindParenthesizedExpression((ParenthesizedExpressionSyntax) syntax);
            case LITERAL_EXPRESSION -> bindLiteralExpression((LiteralExpressionSyntax) syntax);
            case NAME_EXPRESSION -> bindNameExpression((NameExpressionSyntax) syntax);
            case ASSIGNMENT_EXPRESSION -> bindAssignmentExpression((AssignmentExpressionSyntax) syntax);
            case UNARY_EXPRESSION -> bindUnaryExpression((UnaryExpressionSyntax) syntax);
            case BINARY_EXPRESSION -> bindBinaryExpression((BinaryExpressionSyntax) syntax);
            default -> throw new Exception("ERROR: unexpected syntax: " + syntax.getKind());
        };
    }


    private BoundExpression bindParenthesizedExpression(ParenthesizedExpressionSyntax syntax) throws Exception {
        return bindExpression(syntax.getExpression());
    }

    private BoundExpression bindNameExpression(NameExpressionSyntax syntax) {
        String name = syntax.getIdentifierToken().getText();
        VariableSymbol variable = scope.getVariableByIdentifier(name);
        if (variable != null) {
            return new BoundVariableExpression(variable);
        }
        diagnostics.reportUndefinedNameExpression(syntax.getIdentifierToken().getSpan(), name);
        return new BoundLiteralExpression(0);
    }

    private BoundExpression bindAssignmentExpression(AssignmentExpressionSyntax syntax) throws Exception {
        String name = syntax.getIdentifierToken().getText();
        BoundExpression boundExpression = bindExpression(syntax.getExpression());
        if (!scope.isVariablePresent(name)) {
            diagnostics.reportUndefinedNameExpression(syntax.getIdentifierToken().getSpan(), name);
            return boundExpression;

        }
        VariableSymbol variable = scope.getVariableByIdentifier(name);
        scope.declareVariable(variable);
        if (variable.readOnly()) {
            diagnostics.reportReadOnly(syntax.getEqualsToken().getSpan(), name);
        }
        if (!boundExpression.getType().equals(variable.type())) {
            diagnostics.reportCannotConvert(syntax.getExpression().getSpan(), boundExpression.getType(), variable.type());
            return boundExpression;
        }
        return new BoundAssignmentExpression(variable, boundExpression);
    }


    private BoundExpression bindLiteralExpression(LiteralExpressionSyntax syntax) {
        Object value = syntax.getValue() != null ? syntax.getValue() : 0;
        return new BoundLiteralExpression(value);
    }

    private BoundExpression bindUnaryExpression(UnaryExpressionSyntax syntax) throws Exception {
        BoundExpression right = bindExpression(syntax.getRight());
        BoundUnaryOperator operator = BoundUnaryOperator.bind(syntax.getOperatorToken().getKind(), right.getType());
        if (operator == null) {
            diagnostics.reportUndefinedUnaryOperator(syntax.getOperatorToken().getSpan(),
                    syntax.getOperatorToken().getText(), right.getType());
            return right;
        }
        return new BoundUnaryExpression(operator, right);
    }

    private BoundExpression bindBinaryExpression(BinaryExpressionSyntax syntax) throws Exception {
        BoundExpression left = bindExpression(syntax.getLeft());
        BoundExpression right = bindExpression(syntax.getRight());
        BoundBinaryOperator operator = BoundBinaryOperator.bind(syntax.getOperatorToken().getKind(), left.getType(), right.getType());
        if (operator == null) {
            diagnostics.reportUndefinedBinaryOperator(syntax.getOperatorToken().getSpan(),
                    syntax.getOperatorToken().getText(), left.getType(), right.getType());

            return left;
        }
        return new BoundBinaryExpression(left, operator, right);
    }
}
