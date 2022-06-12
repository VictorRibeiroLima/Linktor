package codeanalysis.syntax.statements;

import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;
import codeanalysis.syntax.SyntaxTree;
import codeanalysis.syntax.expression.ExpressionSyntax;

import java.util.List;

public class ReturnStatementSyntax extends StatementSyntax {
    private final SyntaxToken keyword;
    private final ExpressionSyntax expression;

    private final SyntaxKind kind;
    private final List<SyntaxNode> children;

    public ReturnStatementSyntax(SyntaxTree tree, SyntaxToken keyword, ExpressionSyntax expression) {
        super(tree);
        this.keyword = keyword;
        this.expression = expression;
        this.kind = SyntaxKind.RETURN_STATEMENT;
        if (expression != null)
            this.children = List.of(expression);
        else
            this.children = List.of();
    }

    public SyntaxToken getKeyword() {
        return keyword;
    }

    public ExpressionSyntax getExpression() {
        return expression;
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
