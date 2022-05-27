package src.codeanalysis.syntax.expression;

import src.codeanalysis.syntax.SyntaxKind;
import src.codeanalysis.syntax.SyntaxNode;
import src.codeanalysis.syntax.SyntaxToken;

import java.util.Arrays;
import java.util.List;

public class UnaryExpressionSyntax extends ExpressionSyntax {
    private final SyntaxToken operatorToken;
    private final ExpressionSyntax right;
    private final SyntaxKind kind;

    public UnaryExpressionSyntax(SyntaxToken operatorToken, ExpressionSyntax right) {
        this.operatorToken = operatorToken;
        this.right = right;
        this.kind = SyntaxKind.UNARY_EXPRESSION;
    }

    public SyntaxToken getOperatorToken() {
        return operatorToken;
    }

    public ExpressionSyntax getRight() {
        return right;
    }

    @Override
    public SyntaxKind getKind() {
        return this.kind;
    }

    @Override
    public List<SyntaxNode> getChildren() {
        return Arrays.asList(operatorToken, right);
    }
}
