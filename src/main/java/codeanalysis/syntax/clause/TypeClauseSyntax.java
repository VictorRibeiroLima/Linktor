package codeanalysis.syntax.clause;

import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;

import java.util.List;

public class TypeClauseSyntax extends SyntaxNode {
    private final SyntaxToken colonToken;
    private final SyntaxToken identifierToken;
    private final SyntaxKind kind;
    private final List<SyntaxNode> children;

    public TypeClauseSyntax(SyntaxToken colonToken, SyntaxToken identifierToken) {
        this.colonToken = colonToken;
        this.identifierToken = identifierToken;
        this.kind = SyntaxKind.TYPE_CLAUSE;
        this.children = List.of(colonToken, identifierToken);
    }

    public SyntaxToken getColonToken() {
        return colonToken;
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
