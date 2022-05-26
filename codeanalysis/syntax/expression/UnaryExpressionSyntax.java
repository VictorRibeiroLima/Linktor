package codeanalysis.syntax.expression;

import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;
import codeanalysis.syntax.SyntaxType;

import java.util.Arrays;
import java.util.List;

public class UnaryExpressionSyntax extends ExpressionSyntax {
    private final SyntaxToken operatorToken;
    private final ExpressionSyntax right;

    public UnaryExpressionSyntax(SyntaxToken operatorToken, ExpressionSyntax right) {
        this.operatorToken = operatorToken;
        this.right = right;
    }

    public SyntaxToken getOperatorToken() {
        return operatorToken;
    }

    public ExpressionSyntax getRight() {
        return right;
    }

    @Override
    public SyntaxType getType() {
        return SyntaxType.UNARY_EXPRESSION;
    }

    @Override
    public List<SyntaxNode> getChildren() {
        return Arrays.asList(operatorToken, right);
    }
}
