package codeanalysis.evaluator;

import codeanalysis.binding.expression.assignment.BoundAssignmentExpression;
import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.binding.expression.binary.BoundBinaryExpression;
import codeanalysis.binding.expression.literal.BoundLiteralExpression;
import codeanalysis.binding.expression.unary.BoundUnaryExpression;
import codeanalysis.binding.expression.variable.BoundVariableExpression;
import codeanalysis.symbol.VariableSymbol;

import java.util.Map;

public final class Evaluator {
    private final BoundExpression root;
    private final Map<VariableSymbol, Object> variables;

    public Evaluator(BoundExpression root, Map<VariableSymbol, Object> variables) {
        this.root = root;
        this.variables = variables;
    }

    public Object evaluate() throws Exception {
        return evaluateExpression(root);
    }

    private Object evaluateExpression(BoundExpression node) throws Exception {
        if (node instanceof BoundLiteralExpression l)
            return l.getValue();
        if (node instanceof BoundVariableExpression v)
            return variables.get(v.getVariable());
        if (node instanceof BoundAssignmentExpression a) {
            Object value = evaluateExpression(a.getBoundExpression());
            variables.put(a.getVariable(), value);
            return value;
        }
        if (node instanceof BoundUnaryExpression u) {
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
        if (node instanceof BoundBinaryExpression b) {
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
        throw new Exception("Unexpected node " + node.getKind());
    }
}
