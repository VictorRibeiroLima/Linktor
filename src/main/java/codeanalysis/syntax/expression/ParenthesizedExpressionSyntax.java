package codeanalysis.syntax.expression;

import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;

import java.util.Arrays;
import java.util.List;

public class ParenthesizedExpressionSyntax extends ExpressionSyntax {
    private final SyntaxToken open;
    private final ExpressionSyntax expression;
    private final SyntaxToken close;

    private final SyntaxKind kind;

    public ParenthesizedExpressionSyntax(SyntaxToken open, ExpressionSyntax expression, SyntaxToken close) {
        this.open = open;
        this.expression = expression;
        this.close = close;
        this.kind = SyntaxKind.PARENTHESIZED_EXPRESSION;
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
        return Arrays.asList(open, expression, close);
    }
}
