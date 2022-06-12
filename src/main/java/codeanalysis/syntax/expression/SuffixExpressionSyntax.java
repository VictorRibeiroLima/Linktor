package codeanalysis.syntax.expression;

import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;
import codeanalysis.syntax.SyntaxTree;

import java.util.List;

public class SuffixExpressionSyntax extends ExpressionSyntax {
    private final SyntaxToken identifier;
    private final SyntaxToken token;
    private final SyntaxKind kind;
    private final List<SyntaxNode> children;

    public SuffixExpressionSyntax(SyntaxTree tree, SyntaxToken identifier, SyntaxToken token) {
        super(tree);
        this.identifier = identifier;
        this.token = token;
        this.kind = SyntaxKind.SUFFIX_EXPRESSION;
        this.children = List.of(identifier, token);
    }

    public SyntaxToken getIdentifier() {
        return identifier;
    }

    public SyntaxToken getToken() {
        return token;
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
