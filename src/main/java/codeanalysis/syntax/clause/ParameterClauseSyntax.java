package codeanalysis.syntax.clause;

import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;

import java.util.List;

public class ParameterClauseSyntax extends SyntaxNode {
    private final SyntaxToken identifier;
    private final TypeClauseSyntax type;
    private final SyntaxKind kind;
    private final List<SyntaxNode> children;

    public ParameterClauseSyntax(SyntaxToken identifier, TypeClauseSyntax type) {
        this.identifier = identifier;
        this.type = type;
        this.kind = SyntaxKind.PARAMETER_CLAUSE;
        this.children = List.of(identifier, type);
    }

    public SyntaxToken getIdentifier() {
        return identifier;
    }

    public TypeClauseSyntax getType() {
        return type;
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
