package codeanalysis.syntax.expression;

import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;

import java.util.List;

public class OperationAssignmentExpressionSyntax extends ExpressionSyntax {
    private final SyntaxToken identifierToken;

    private final SyntaxToken operationToken;

    private final ExpressionSyntax expression;

    private final SyntaxKind kind;

    private final List<SyntaxNode> children;

    public OperationAssignmentExpressionSyntax(SyntaxToken identifierToken, SyntaxToken equalsToken, ExpressionSyntax expression) {
        this.identifierToken = identifierToken;
        this.operationToken = equalsToken;
        this.expression = expression;
        this.kind = SyntaxKind.OPERATION_ASSIGNMENT_EXPRESSION;
        this.children = List.of(identifierToken, equalsToken, expression);
    }

    public SyntaxToken getIdentifierToken() {
        return identifierToken;
    }

    public SyntaxToken getOperationToken() {
        return operationToken;
    }

    public ExpressionSyntax getExpression() {
        return expression;
    }

    public SyntaxKind getKind() {
        return kind;
    }

    @Override
    public List<SyntaxNode> getChildren() {
        return children;
    }
}
