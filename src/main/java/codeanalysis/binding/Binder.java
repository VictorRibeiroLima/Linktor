package codeanalysis.binding;

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
import codeanalysis.diagnostics.Diagnostic;
import codeanalysis.diagnostics.DiagnosticBag;
import codeanalysis.symbol.VariableSymbol;
import codeanalysis.syntax.CompilationUnitSyntax;
import codeanalysis.syntax.expression.*;

import java.util.List;
import java.util.Stack;

public class Binder {

    private final DiagnosticBag diagnostics = new DiagnosticBag();

    private final BoundScope scope;

    private Binder(BoundScope parent) {
        scope = new BoundScope(parent);
    }

    public DiagnosticBag getDiagnostics() {
        return diagnostics;
    }

    public static BoundGlobalScope boundGlobalScope(CompilationUnitSyntax unit, BoundGlobalScope previous) throws Exception {
        BoundScope parent = createParentScope(previous);
        Binder binder = new Binder(parent);
        BoundExpression expression = binder.bindExpression(unit.getExpression());
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
                scope.declare(variable);
            }
            parentScope = scope;
        }
        return parentScope;
    }

    public BoundExpression bindExpression(ExpressionSyntax syntax) throws Exception {
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

    private BoundExpression bindNameExpression(NameExpressionSyntax syntax) throws Exception {
        String name = syntax.getIdentifierToken().getText();
        VariableSymbol variable = new VariableSymbol(name, null);
        variable = scope.lookup(variable);
        if (variable != null) {
            return new BoundVariableExpression(variable);
        }
        diagnostics.reportUndefinedNameExpression(syntax.getIdentifierToken().getSpan(), name);
        return new BoundLiteralExpression(0);
    }

    private BoundExpression bindAssignmentExpression(AssignmentExpressionSyntax syntax) throws Exception {
        String name = syntax.getIdentifierToken().getText();
        BoundExpression boundExpression = bindExpression(syntax.getExpression());
        VariableSymbol variable = new VariableSymbol(name, boundExpression.getType());
        if (!scope.declare(variable)) {
            diagnostics.reportVariableAlreadyDeclared(name, syntax.getIdentifierToken().getSpan());
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
