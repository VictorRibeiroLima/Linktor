package codeanalysis.syntax.expression;

import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;
import codeanalysis.syntax.SyntaxTree;

import java.util.Arrays;
import java.util.List;

public class BinaryExpressionSyntax extends ExpressionSyntax {
    private final SyntaxToken operatorToken;
    private final ExpressionSyntax left;
    private final ExpressionSyntax right;

    private final SyntaxKind kind;

    public BinaryExpressionSyntax(SyntaxTree tree, ExpressionSyntax left, SyntaxToken operatorToken, ExpressionSyntax right) {
        super(tree);
        this.left = left;
        this.operatorToken = operatorToken;
        this.right = right;
        this.kind = SyntaxKind.BINARY_EXPRESSION;
    }

    public SyntaxToken getOperatorToken() {
        return operatorToken;
    }

    public ExpressionSyntax getLeft() {
        return left;
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
        return Arrays.asList(left, operatorToken, right);
    }
}
