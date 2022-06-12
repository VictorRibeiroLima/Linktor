package codeanalysis.syntax.expression;

import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;
import codeanalysis.syntax.SyntaxTree;

import java.util.List;

public class ParenthesizedExpressionSyntax extends ExpressionSyntax {
    private final SyntaxToken open;
    private final ExpressionSyntax expression;
    private final SyntaxToken close;

    private final SyntaxKind kind;

    private final List<SyntaxNode> children;

    public ParenthesizedExpressionSyntax(SyntaxTree tree, SyntaxToken open, ExpressionSyntax expression, SyntaxToken close) {
        super(tree);
        this.open = open;
        this.expression = expression;
        this.close = close;
        this.kind = SyntaxKind.PARENTHESIZED_EXPRESSION;
        this.children = List.of(open, expression, close);
    }

    public ExpressionSyntax getExpression() {
        return expression;
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
