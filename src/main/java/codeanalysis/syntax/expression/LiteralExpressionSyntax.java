package src.codeanalysis.syntax.expression;

import src.codeanalysis.syntax.SyntaxKind;
import src.codeanalysis.syntax.SyntaxNode;
import src.codeanalysis.syntax.SyntaxToken;

import java.util.Arrays;
import java.util.List;

public class LiteralExpressionSyntax extends ExpressionSyntax {
    private final SyntaxToken token;

    private final SyntaxKind kind;

    private final Object value;

    public LiteralExpressionSyntax(SyntaxToken token, Object value) {
        this.token = token;
        this.kind = SyntaxKind.LITERAL_EXPRESSION;
        this.value = value;
    }

    public LiteralExpressionSyntax(SyntaxToken token) {
        this.token = token;
        this.kind = SyntaxKind.LITERAL_EXPRESSION;
        this.value = this.token.getValue();
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
        return Arrays.asList(token);
    }
}
