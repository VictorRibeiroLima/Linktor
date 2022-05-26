package codeanalysis.evaluator;

import codeanalysis.syntax.expression.*;

public final class Evaluator {
    private final ExpressionSyntax root;

    public Evaluator(ExpressionSyntax root) {
        this.root = root;
    }

    public int evaluate() throws Exception {
        return evaluateExpression(root);
    }

    private int evaluateExpression(ExpressionSyntax node) throws Exception {
        if (node instanceof LiteralExpressionSyntax n)
            return (int) n.getToken().getValue();
        if (node instanceof UnaryExpressionSyntax u) {
            int value = evaluateExpression(u.getRight());
            switch (u.getOperatorToken().getKind()) {
                case PLUS_TOKEN:
                    return value;
                case MINUS_TOKEN:
                    return -value;
                default:
                    throw new Exception("Unexpected unary operation " + u.getOperatorToken().getKind().toString());
            }
        }
        if (node instanceof BinaryExpressionSyntax b) {
            int left = evaluateExpression(b.getLeft());
            int right = evaluateExpression(b.getRight());
            switch (b.getOperatorToken().getKind()) {
                case PLUS_TOKEN:
                    return left + right;
                case MINUS_TOKEN:
                    return left - right;
                case DIVISION_TOKEN:
                    return left / right;
                case MULTIPLICATION_TOKEN:
                    return left * right;
                default:
                    throw new Exception("Unexpected binary operation " + b.getOperatorToken().getKind().toString());
            }
        }
        if (node instanceof ParenthesizedExpressionSyntax p) {
            return evaluateExpression(p.getExpression());
        }
        throw new Exception("Unexpected node " + node.getKind());
    }
}
