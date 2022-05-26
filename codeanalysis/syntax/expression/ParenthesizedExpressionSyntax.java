package codeanalysis.syntax.expression;

import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;
import codeanalysis.syntax.SyntaxType;

import java.util.Arrays;
import java.util.List;

public class ParenthesizedExpressionSyntax extends ExpressionSyntax {
    private final SyntaxToken open;
    private final ExpressionSyntax expression;
    private final SyntaxToken close;

    public ParenthesizedExpressionSyntax(SyntaxToken open, ExpressionSyntax expression, SyntaxToken close) {
        this.open = open;
        this.expression = expression;
        this.close = close;
    }

    public ExpressionSyntax getExpression() {
        return expression;
    }

    @Override
    public SyntaxType getType() {
        return SyntaxType.PARENTHESIZED_EXPRESSION;
    }

    @Override
    public List<SyntaxNode> getChildren() {
        return Arrays.asList(open, expression, close);
    }
}
