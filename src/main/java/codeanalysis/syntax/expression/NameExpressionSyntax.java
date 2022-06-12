package codeanalysis.syntax.expression;

import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;
import codeanalysis.syntax.SyntaxTree;

import java.util.List;

public class NameExpressionSyntax extends ExpressionSyntax {
    private final SyntaxToken identifierToken;

    private final SyntaxKind kind;
    private final List<SyntaxNode> children;

    public NameExpressionSyntax(SyntaxTree tree, SyntaxToken identifierToken) {
        super(tree);
        this.identifierToken = identifierToken;
        this.kind = SyntaxKind.NAME_EXPRESSION;
        this.children = List.of(identifierToken);
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
        return children;
    }
}
