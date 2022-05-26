package codeanalysis.evaluator;

import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.binding.expression.binary.BoundBinaryExpression;
import codeanalysis.binding.expression.literal.BoundLiteralExpression;
import codeanalysis.binding.expression.unary.BoundUnaryExpression;

public final class Evaluator {
    private final BoundExpression root;

    public Evaluator(BoundExpression root) {
        this.root = root;
    }

    public Object evaluate() throws Exception {
        return evaluateExpression(root);
    }

    private Object evaluateExpression(BoundExpression node) throws Exception {
        if (node instanceof BoundLiteralExpression l)
            return l.getValue();
        if (node instanceof BoundUnaryExpression u) {
            int value = (int) evaluateExpression(u.getRight());
            switch (u.getOperatorKind()) {
                case IDENTITY:
                    return value;
                case NEGATION:
                    return -value;
                default:
                    throw new Exception("Unexpected unary operation " + u.getOperatorKind());
            }
        }
        if (node instanceof BoundBinaryExpression b) {
            int left = (int) evaluateExpression(b.getLeft());
            int right = (int) evaluateExpression(b.getRight());
            switch (b.getOperatorKind()) {
                case ADDITION:
                    return left + right;
                case SUBTRACTION:
                    return left - right;
                case DIVISION:
                    return left / right;
                case MULTIPLICATION:
                    return left * right;
                default:
                    throw new Exception("Unexpected binary operation " + b.getOperatorKind());
            }
        }
        throw new Exception("Unexpected node " + node.getKind());
    }
}
