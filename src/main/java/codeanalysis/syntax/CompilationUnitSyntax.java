package codeanalysis.syntax;

import codeanalysis.syntax.expression.ExpressionSyntax;

import java.util.Arrays;
import java.util.List;

public class CompilationUnitSyntax extends SyntaxNode {
    private final SyntaxKind kind;
    private final ExpressionSyntax expression;
    private final SyntaxToken endOfFileToken;

    public CompilationUnitSyntax(ExpressionSyntax expression, SyntaxToken endOfFileToken) {
        this.kind = SyntaxKind.COMPILATION_UNIT;
        this.expression = expression;
        this.endOfFileToken = endOfFileToken;
    }

    public ExpressionSyntax getExpression() {
        return expression;
    }

    public SyntaxToken getEndOfFileToken() {
        return endOfFileToken;
    }

    @Override
    public SyntaxKind getKind() {
        return kind;
    }

    @Override
    public List<SyntaxNode> getChildren() {
        return Arrays.asList(expression, endOfFileToken);
    }
}
