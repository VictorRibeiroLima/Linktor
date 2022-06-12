package codeanalysis.syntax.expression;

import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;
import codeanalysis.syntax.SyntaxTree;

import java.util.List;

public class UnaryExpressionSyntax extends ExpressionSyntax {
    private final SyntaxToken operatorToken;
    private final ExpressionSyntax right;
    private final SyntaxKind kind;

    private final List<SyntaxNode> children;

    public UnaryExpressionSyntax(SyntaxTree tree, SyntaxToken operatorToken, ExpressionSyntax right) {
        super(tree);
        this.operatorToken = operatorToken;
        this.right = right;
        this.kind = SyntaxKind.UNARY_EXPRESSION;
        this.children = List.of(operatorToken, right);

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
        return children;
    }
}
