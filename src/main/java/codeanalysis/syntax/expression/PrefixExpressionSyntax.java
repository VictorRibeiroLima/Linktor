package codeanalysis.syntax.expression;

import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;

import java.util.List;

public class PrefixExpressionSyntax extends ExpressionSyntax {
    private final SyntaxToken token;
    private final SyntaxToken identifier;

    private final SyntaxKind kind;

    private final List<SyntaxNode> children;

    public PrefixExpressionSyntax(SyntaxToken token, SyntaxToken identifier) {
        this.token = token;
        this.identifier = identifier;
        this.kind = SyntaxKind.PREFIX_EXPRESSION;
        this.children = List.of(token, identifier);
    }

    public SyntaxToken getToken() {
        return token;
    }

    public SyntaxToken getIdentifier() {
        return identifier;
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
