package src.codeanalysis.syntax.expression;

import src.codeanalysis.syntax.SyntaxKind;
import src.codeanalysis.syntax.SyntaxNode;
import src.codeanalysis.syntax.SyntaxToken;

import java.util.Arrays;
import java.util.List;

public class AssignmentExpressionSyntax extends ExpressionSyntax {
    private final SyntaxToken identifierToken;

    private final SyntaxToken equalsToken;

    private final ExpressionSyntax expression;

    private final SyntaxKind kind;

    public AssignmentExpressionSyntax(SyntaxToken identifierToken, SyntaxToken equalsToken, ExpressionSyntax expression) {
        this.identifierToken = identifierToken;
        this.equalsToken = equalsToken;
        this.expression = expression;
        this.kind = SyntaxKind.ASSIGNMENT_EXPRESSION;
    }

    public SyntaxToken getIdentifierToken() {
        return identifierToken;
    }

    public SyntaxToken getEqualsToken() {
        return equalsToken;
    }

    public ExpressionSyntax getExpression() {
        return expression;
    }

    public SyntaxKind getKind() {
        return kind;
    }

    @Override
    public List<SyntaxNode> getChildren() {
        return Arrays.asList(identifierToken, equalsToken, expression);
    }
}
