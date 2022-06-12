package codeanalysis.syntax.expression;

import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;
import codeanalysis.syntax.SyntaxTree;

import java.util.List;

public class LiteralExpressionSyntax extends ExpressionSyntax {
    private final SyntaxToken token;

    private final SyntaxKind kind;

    private final Object value;

    private final List<SyntaxNode> children;

    public LiteralExpressionSyntax(SyntaxTree tree, SyntaxToken token) {
        this(tree, token, token.getValue());
    }

    public LiteralExpressionSyntax(SyntaxTree tree, SyntaxToken token, Object value) {
        super(tree);
        this.token = token;
        this.kind = SyntaxKind.LITERAL_EXPRESSION;
        this.value = value;
        this.children = List.of(token);
    }

    @Override
    public SyntaxKind getKind() {
        return this.kind;
    }


    public SyntaxToken getToken() {
        return token;
    }

    public Object getValue() {
        return value;
    }

    public List<SyntaxNode> getChildren() {
        return children;
    }
}
