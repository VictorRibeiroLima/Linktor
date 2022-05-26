package codeanalysis.syntax.expression;

import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;

import java.util.Arrays;
import java.util.List;

public class LiteralExpressionSyntax extends ExpressionSyntax {
    private final SyntaxToken token;

    private final SyntaxKind kind;

    public LiteralExpressionSyntax(SyntaxToken token) {
        this.token = token;
        this.kind = SyntaxKind.LITERAL_EXPRESSION;
    }

    @Override
    public SyntaxKind getKind() {
        return this.kind;
    }


    public SyntaxToken getToken() {
        return token;
    }

    public List<SyntaxNode> getChildren() {
        return Arrays.asList(token);
    }
}
