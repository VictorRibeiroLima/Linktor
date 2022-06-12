package codeanalysis.syntax.statements;

import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;
import codeanalysis.syntax.SyntaxTree;
import codeanalysis.syntax.clause.ForConditionClauseSyntax;

import java.util.ArrayList;
import java.util.List;

public class ForStatementSyntax extends StatementSyntax {
    private final SyntaxToken forKeyword;


    private final ForConditionClauseSyntax condition;

    private final StatementSyntax thenStatement;


    private final SyntaxKind kind;

    private final List<SyntaxNode> children;

    public ForStatementSyntax(SyntaxTree tree, SyntaxToken forKeyword, ForConditionClauseSyntax condition, StatementSyntax thenStatement) {
        super(tree);
        this.forKeyword = forKeyword;
        this.condition = condition;
        this.thenStatement = thenStatement;
        this.kind = SyntaxKind.FOR_STATEMENT;
        List<SyntaxNode> children = new ArrayList<>();
        children.add(forKeyword);
        children.add(condition);
        children.add(thenStatement);
        this.children = List.copyOf(children);
    }

    public SyntaxToken getForKeyword() {
        return forKeyword;
    }

    public ForConditionClauseSyntax getCondition() {
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
