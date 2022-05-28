package src.codeanalysis.evaluator;

import src.codeanalysis.binding.expression.BoundExpression;
import src.codeanalysis.binding.expression.binary.BoundBinaryExpression;
import src.codeanalysis.binding.expression.literal.BoundLiteralExpression;
import src.codeanalysis.binding.expression.unary.BoundUnaryExpression;

import java.util.Map;

public final class Evaluator {
    private final BoundExpression root;
    private final Map<String, Object> variables;

    public Evaluator(BoundExpression root, Map<String, Object> variables) {
        this.root = root;
        this.variables = variables;
    }

    public Object evaluate() throws Exception {
        return evaluateExpression(root);
    }

    private Object evaluateExpression(BoundExpression node) throws Exception {
        if (node instanceof BoundLiteralExpression l)
            return l.getValue();
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
