package codeanalysis.syntax.statements;

import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.expression.ExpressionSyntax;

import java.util.Arrays;
import java.util.List;

public class ExpressionStatementSyntax extends StatementSyntax {
    private final ExpressionSyntax expression;

    private final SyntaxKind kind;

    public ExpressionStatementSyntax(ExpressionSyntax expression) {
        this.expression = expression;
        this.kind = SyntaxKind.EXPRESSION_STATEMENT;
    }

    public ExpressionSyntax getExpression() {
        return expression;
    }

    @Override
    public SyntaxKind getKind() {
        return kind;
    }

    @Override
    public List<SyntaxNode> getChildren() {
        return Arrays.asList(expression);
    }
}
