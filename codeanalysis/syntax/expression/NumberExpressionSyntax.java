package codeanalysis.syntax.expression;

import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;
import codeanalysis.syntax.SyntaxType;

import java.util.Arrays;
import java.util.List;

public class NumberExpressionSyntax extends ExpressionSyntax {
    private final SyntaxToken token;

    public NumberExpressionSyntax(SyntaxToken token) {
        this.token = token;
    }

    @Override
    public SyntaxType getType() {
        return SyntaxType.NUMBER_EXPRESSION;
    }


    public SyntaxToken getToken() {
        return token;
    }

    public List<SyntaxNode> getChildren() {
        return Arrays.asList(token);
    }
}
