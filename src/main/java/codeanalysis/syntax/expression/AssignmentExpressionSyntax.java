package codeanalysis.syntax.expression;

import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;

import java.util.List;

public class AssignmentExpressionSyntax extends ExpressionSyntax {
    private final SyntaxToken identifierToken;

    private final SyntaxToken operatorToken;

    private final ExpressionSyntax expression;

    private final SyntaxKind kind;

    private final List<SyntaxNode> children;

    public AssignmentExpressionSyntax(SyntaxToken identifierToken, SyntaxToken operatorToken, ExpressionSyntax expression) {
        this.identifierToken = identifierToken;
        this.operatorToken = operatorToken;
        this.expression = expression;
        this.kind = SyntaxKind.ASSIGNMENT_EXPRESSION;
        this.children = List.of(identifierToken, operatorToken, expression);
    }

    public SyntaxToken getIdentifierToken() {
        return identifierToken;
    }

    public SyntaxToken getOperatorToken() {
        return operatorToken;
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
