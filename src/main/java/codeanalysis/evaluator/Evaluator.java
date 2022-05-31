package codeanalysis.evaluator;

import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.binding.expression.assignment.BoundAssignmentExpression;
import codeanalysis.binding.expression.binary.BoundBinaryExpression;
import codeanalysis.binding.expression.literal.BoundLiteralExpression;
import codeanalysis.binding.expression.unary.BoundUnaryExpression;
import codeanalysis.binding.expression.variable.BoundVariableExpression;
import codeanalysis.binding.statement.BoundBlockStatement;
import codeanalysis.binding.statement.BoundExpressionStatement;
import codeanalysis.binding.statement.BoundStatement;
import codeanalysis.symbol.VariableSymbol;

import java.util.Map;

public final class Evaluator {
    private final BoundStatement root;
    private final Map<VariableSymbol, Object> variables;

    private Object lastValue;

    public Evaluator(BoundStatement root, Map<VariableSymbol, Object> variables) {
        this.root = root;
        this.variables = variables;
    }

    public Object evaluate() throws Exception {
        evaluateStatement(root);
        return lastValue;
    }

    private void evaluateStatement(BoundStatement statement) throws Exception {
        switch (statement.getKind()) {
            case BLOCK_STATEMENT -> {
                evaluateBlockStatement((BoundBlockStatement) statement);
                break;
            }
            case EXPRESSION_STATEMENT -> {
                evaluateExpressionStatement((BoundExpressionStatement) statement);
                break;
            }
            default -> {
                throw new Exception("Unexpected node " + statement.getKind());

            }
        }
    }

    private void evaluateBlockStatement(BoundBlockStatement statement) throws Exception {
        for (BoundStatement b : statement.getStatements()) {
            evaluateStatement(b);
        }
    }

    private void evaluateExpressionStatement(BoundExpressionStatement statement) throws Exception {
        Object result = evaluateExpression(statement.getExpression());
        lastValue = result;
    }

    private Object evaluateExpression(BoundExpression node) throws Exception {
        return switch (node.getKind()) {
            case LITERAL_EXPRESSION -> evaluateLiteralExpression((BoundLiteralExpression) node);
            case VARIABLE_EXPRESSION -> evaluateVariableExpression((BoundVariableExpression) node);
            case ASSIGNMENT_EXPRESSION -> evaluateAssignmentExpression((BoundAssignmentExpression) node);
            case UNARY_EXPRESSION -> evaluateUnaryExpression((BoundUnaryExpression) node);
            case BINARY_EXPRESSION -> evaluateBinaryExpression((BoundBinaryExpression) node);
            default -> throw new Exception("Unexpected node " + node.getKind());
        };
    }

    private Object evaluateLiteralExpression(BoundLiteralExpression l) {
        return l.getValue();
    }

    private Object evaluateVariableExpression(BoundVariableExpression v) {
        Object variable = variables.get(v.getVariable());
        return variable;
    }

    private Object evaluateAssignmentExpression(BoundAssignmentExpression a) throws Exception {
        Object value = evaluateExpression(a.getBoundExpression());
        variables.put(a.getVariable(), value);
        return value;
    }

    private Object evaluateUnaryExpression(BoundUnaryExpression u) throws Exception {
        Object value = evaluateExpression(u.getRight());
        switch (u.getOperator().getKind()) {
            case IDENTITY:
                return value;
            case NEGATION:
                return -(int) value;
            case LOGICAL_NEGATION:
                return !(boolean) value;
            default:
                throw new Exception("Unexpected unary operation " + u.getOperator());
        }
    }

    private Object evaluateBinaryExpression(BoundBinaryExpression b) throws Exception {
        Object left = evaluateExpression(b.getLeft());
        Object right = evaluateExpression(b.getRight());
        switch (b.getOperator().getKind()) {
            case LOGICAL_AND:
                return (boolean) left && (boolean) right;
            case LOGICAL_OR:
                return (boolean) left || (boolean) right;
            case LOGICAL_EQUALITY:
                return left.equals(right);
            case LOGICAL_INEQUALITY:
                return !left.equals(right);
            case ADDITION:
                return (int) left + (int) right;
            case SUBTRACTION:
                return (int) left - (int) right;
            case DIVISION:
                return (int) left / (int) right;
            case MULTIPLICATION:
                return (int) left * (int) right;
            default:
                throw new Exception("Unexpected binary operation " + b.getOperator());
        }
    }
}
