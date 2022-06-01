package codeanalysis.syntax.statements;

import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;
import codeanalysis.syntax.expression.ExpressionSyntax;

import java.util.ArrayList;
import java.util.List;

public class WhileStatementSyntax extends StatementSyntax {
    private final SyntaxToken whileKeyword;

    private final ExpressionSyntax condition;

    private final StatementSyntax thenStatement;


    private final SyntaxKind kind;

    private final List<SyntaxNode> children;

    public WhileStatementSyntax(SyntaxToken whileKeyword, ExpressionSyntax condition, StatementSyntax thenStatement) {
        this.whileKeyword = whileKeyword;
        this.condition = condition;
        this.thenStatement = thenStatement;
        this.kind = SyntaxKind.WHILE_STATEMENT;
        List<SyntaxNode> children = new ArrayList<>();
        children.add(whileKeyword);
        children.add(condition);
        children.add(thenStatement);
        this.children = List.copyOf(children);
    }

    public SyntaxToken getWhileKeyworld() {
        return whileKeyword;
    }

    public ExpressionSyntax getCondition() {
        return condition;
    }

    public StatementSyntax getThenStatement() {
        return thenStatement;
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
