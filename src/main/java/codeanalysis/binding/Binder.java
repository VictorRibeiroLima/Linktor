package codeanalysis.binding;

import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.binding.expression.assignment.BoundAssignmentExpression;
import codeanalysis.binding.expression.binary.BoundBinaryExpression;
import codeanalysis.binding.expression.binary.BoundBinaryOperator;
import codeanalysis.binding.expression.call.BoundCallExpression;
import codeanalysis.binding.expression.error.BoundErrorExpression;
import codeanalysis.binding.expression.literal.BoundLiteralExpression;
import codeanalysis.binding.expression.unary.BoundUnaryExpression;
import codeanalysis.binding.expression.unary.BoundUnaryOperator;
import codeanalysis.binding.expression.variable.BoundVariableExpression;
import codeanalysis.binding.scopes.BoundGlobalScope;
import codeanalysis.binding.scopes.BoundScope;
import codeanalysis.binding.statement.BoundStatement;
import codeanalysis.binding.statement.block.BoundBlockStatement;
import codeanalysis.binding.statement.conditional.BoundElseClause;
import codeanalysis.binding.statement.conditional.BoundIfStatement;
import codeanalysis.binding.statement.declaration.BoundVariableDeclarationStatement;
import codeanalysis.binding.statement.expression.BoundExpressionStatement;
import codeanalysis.binding.statement.loop.BoundForConditionClause;
import codeanalysis.binding.statement.loop.BoundForStatement;
import codeanalysis.binding.statement.loop.BoundWhileStatement;
import codeanalysis.diagnostics.Diagnostic;
import codeanalysis.diagnostics.DiagnosticBag;
import codeanalysis.symbol.*;
import codeanalysis.syntax.CompilationUnitSyntax;
import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.clause.ElseClauseSyntax;
import codeanalysis.syntax.clause.ForConditionClause;
import codeanalysis.syntax.expression.*;
import codeanalysis.syntax.statements.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        BoundStatement expression = binder.bindStatement(unit.getStatement());
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

        BoundExpression conditionExpression = bindExpression(condition.getConditionExpression(), TypeSymbol.BOOLEAN);
        BoundExpression incrementExpression = bindExpression(condition.getIncrementExpression(), TypeSymbol.INTEGER);
        return new BoundForConditionClause(variable, conditionExpression, incrementExpression);
    }

    private BoundStatement bindWhileStatement(WhileStatementSyntax syntax) throws Exception {
        BoundExpression condition = bindExpression(syntax.getCondition(), TypeSymbol.BOOLEAN);
        BoundStatement thenStatement = bindStatement(syntax.getThenStatement());
        return new BoundWhileStatement(condition, thenStatement);
    }


    private BoundStatement bindIfStatement(IfStatementSyntax syntax) throws Exception {
        BoundExpression condition = bindExpression(syntax.getCondition(), TypeSymbol.BOOLEAN);
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
        String name = syntax.getIdentifier().getText() == null ? "?" : syntax.getIdentifier().getText();
        boolean declare = !syntax.getIdentifier().isMissing();
        BoundExpression initializer = bindExpression(syntax.getInitializer());
        VariableSymbol variableSymbol = new VariableSymbol(name, initializer.getType(), isReadOnly);
        if (declare && !scope.declareVariable(variableSymbol))
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
        BoundExpression expression = bindExpression(syntax.getExpression(), true);
        return new BoundExpressionStatement(expression);
    }

    private BoundExpression bindExpression(ExpressionSyntax syntax, TypeSymbol expectedType) throws Exception {
        BoundExpression result = bindExpression(syntax);
        if (
                !result.getType().equals(expectedType) &&
                        !result.getType().equals(TypeSymbol.ERROR) &&
                        !expectedType.equals(TypeSymbol.ERROR)
        ) {
            diagnostics.reportCannotConvert(syntax.getSpan(), expectedType, result.getType());
        }

        return result;
    }

    private BoundExpression bindExpression(ExpressionSyntax syntax) throws Exception {
        return bindExpression(syntax, false);
    }

    private BoundExpression bindExpression(ExpressionSyntax syntax, boolean canBeVoid) throws Exception {
        BoundExpression result = switch (syntax.getKind()) {
            case PARENTHESIZED_EXPRESSION -> bindParenthesizedExpression((ParenthesizedExpressionSyntax) syntax);
            case LITERAL_EXPRESSION -> bindLiteralExpression((LiteralExpressionSyntax) syntax);
            case NAME_EXPRESSION -> bindNameExpression((NameExpressionSyntax) syntax);
            case ASSIGNMENT_EXPRESSION -> bindAssignmentExpression((AssignmentExpressionSyntax) syntax);
            case UNARY_EXPRESSION -> bindUnaryExpression((UnaryExpressionSyntax) syntax);
            case BINARY_EXPRESSION -> bindBinaryExpression((BinaryExpressionSyntax) syntax);
            case CALL_EXPRESSION -> bindCallExpression((CallExpressionSyntax) syntax);
            default -> throw new Exception("ERROR: unexpected syntax: " + syntax.getKind());
        };
        if (!canBeVoid && result.getType() == TypeSymbol.VOID) {
            diagnostics.reportExpressionMustHaveValue(syntax.getSpan());
            return new BoundErrorExpression();
        }
        return result;
    }

    private BoundExpression bindCallExpression(CallExpressionSyntax syntax) throws Exception {
        List<BoundExpression> boundArgs = new ArrayList<>();
        for (ExpressionSyntax arg : syntax.getArgs()) {
            boundArgs.add(bindExpression(arg));

        }
        List<FunctionSymbol> functions = BuildInFunctions.getAll();
        Optional<FunctionSymbol> function = functions
                .stream().filter(functionSymbol -> functionSymbol.getName().equals(syntax.getIdentifier().getText()))
                .findFirst();
        if (function.isEmpty()) {
            diagnostics.reportUndefinedFunction(syntax.getIdentifier().getSpan(), syntax.getIdentifier().getText());
            return new BoundErrorExpression();
        }
        if (function.get().getParameters().size() != syntax.getArgs().getCount()) {
            diagnostics.reportWrongArgumentCount(
                    syntax.getSpan(),
                    syntax.getIdentifier().getText(),
                    function.get().getParameters().size(),
                    syntax.getArgs().getCount());
            return new BoundErrorExpression();
        }
        for (int i = 0; i < syntax.getArgs().getCount(); i++) {
            ParameterSymbol parameter = function.get().getParameters().get(i);
            BoundExpression arg = boundArgs.get(i);
            if (!arg.getType().equals(parameter.getType())) {
                diagnostics.reportWrongArgumentType(
                        syntax.getSpan(),
                        syntax.getIdentifier().getText(),
                        parameter.getName(),
                        parameter.getType(),
                        arg.getType());
                return new BoundErrorExpression();
            }
        }
        return new BoundCallExpression(function.get(), boundArgs);
    }


    private BoundExpression bindParenthesizedExpression(ParenthesizedExpressionSyntax syntax) throws Exception {
        return bindExpression(syntax.getExpression());
    }

    private BoundExpression bindNameExpression(NameExpressionSyntax syntax) {
        String name = syntax.getIdentifierToken().getText();
        if (syntax.getIdentifierToken().isMissing())
            return new BoundErrorExpression();
        VariableSymbol variable = scope.getVariableByIdentifier(name);
        if (variable == null) {
            diagnostics.reportUndefinedNameExpression(syntax.getIdentifierToken().getSpan(), name);
            return new BoundErrorExpression();
        }
        return new BoundVariableExpression(variable);
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
        if (variable.isReadOnly()) {
            diagnostics.reportReadOnly(syntax.getEqualsToken().getSpan(), name);
        }
        if (!boundExpression.getType().equals(variable.getType())) {
            diagnostics.reportCannotConvert(syntax.getExpression().getSpan(), boundExpression.getType(), variable.getType());
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
        if (right.getType().equals(TypeSymbol.ERROR))
            return new BoundErrorExpression();
        BoundUnaryOperator operator = BoundUnaryOperator.bind(syntax.getOperatorToken().getKind(), right.getType());
        if (operator == null) {
            diagnostics.reportUndefinedUnaryOperator(syntax.getOperatorToken().getSpan(),
                    syntax.getOperatorToken().getText(), right.getType());
            return new BoundErrorExpression();
        }
        return new BoundUnaryExpression(operator, right);
    }

    private BoundExpression bindBinaryExpression(BinaryExpressionSyntax syntax) throws Exception {
        BoundExpression left = bindExpression(syntax.getLeft());
        BoundExpression right = bindExpression(syntax.getRight());
        if (left.getType().equals(TypeSymbol.ERROR) || right.getType().equals(TypeSymbol.ERROR))
            return new BoundErrorExpression();
        BoundBinaryOperator operator = BoundBinaryOperator.bind(syntax.getOperatorToken().getKind(), left.getType(), right.getType());
        if (operator == null) {
            diagnostics.reportUndefinedBinaryOperator(syntax.getOperatorToken().getSpan(),
                    syntax.getOperatorToken().getText(), left.getType(), right.getType());

            return new BoundErrorExpression();
        }
        return new BoundBinaryExpression(left, operator, right);
    }
}
