package codeanalysis.syntax.expression;

import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;

import java.util.Arrays;
import java.util.List;

public class NameExpressionSyntax extends ExpressionSyntax {
    private final SyntaxToken identifierToken;

    private final SyntaxKind kind;

    public NameExpressionSyntax(SyntaxToken identifierToken) {
        this.identifierToken = identifierToken;
        this.kind = SyntaxKind.NAME_EXPRESSION;
    }

    public SyntaxToken getIdentifierToken() {
        return identifierToken;
    }

    @Override
    public SyntaxKind getKind() {
        return kind;
    }

    @Override
    public List<SyntaxNode> getChildren() {
        return Arrays.asList(identifierToken);
    }
}
