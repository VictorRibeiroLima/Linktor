package codeanalysis.evaluator;

import codeanalysis.syntax.SyntaxType;
import codeanalysis.syntax.expression.BinaryExpressionSyntax;
import codeanalysis.syntax.expression.ExpressionSyntax;
import codeanalysis.syntax.expression.LiteralExpressionSyntax;
import codeanalysis.syntax.expression.ParenthesizedExpressionSyntax;

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
        if (node instanceof BinaryExpressionSyntax b) {
            int left = evaluateExpression(b.getLeft());
            int right = evaluateExpression(b.getRight());
            if (b.getOperatorToken().getType() == SyntaxType.PLUS_TOKEN)
                return left + right;
            else if (b.getOperatorToken().getType() == SyntaxType.MINUS_TOKEN)
                return left - right;
            else if (b.getOperatorToken().getType() == SyntaxType.DIVISION_TOKEN)
                return left / right;
            else if (b.getOperatorToken().getType() == SyntaxType.MULTIPLICATION_TOKEN)
                return left * right;
            throw new Exception("Unexpected binary operation " + b.getOperatorToken().getType().toString());
        }
        if (node instanceof ParenthesizedExpressionSyntax p) {
            return evaluateExpression(p.getExpression());
        }
        throw new Exception("Unexpected node " + node.getType());
    }
}
