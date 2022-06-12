package codeanalysis.syntax.statements;

import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;
import codeanalysis.syntax.SyntaxTree;
import codeanalysis.syntax.clause.ElseClauseSyntax;
import codeanalysis.syntax.expression.ExpressionSyntax;

import java.util.ArrayList;
import java.util.List;

public class IfStatementSyntax extends StatementSyntax {
    private final SyntaxToken ifKeyword;

    private final ExpressionSyntax condition;

    private final StatementSyntax thenStatement;

    private final ElseClauseSyntax elseClause;

    private final SyntaxKind kind;

    private final List<SyntaxNode> children;

    public IfStatementSyntax(SyntaxTree tree, SyntaxToken ifKeyword,
                             ExpressionSyntax condition,
                             StatementSyntax thenStatement,
                             ElseClauseSyntax elseClause) {
        super(tree);
        this.ifKeyword = ifKeyword;
        this.condition = condition;
        this.thenStatement = thenStatement;
        this.kind = SyntaxKind.IF_STATEMENT;
        this.elseClause = elseClause;
        List<SyntaxNode> children = new ArrayList<>();
        children.add(ifKeyword);
        children.add(condition);
        children.add(thenStatement);
        if (elseClause != null) {
            children.add(elseClause);
        }
        this.children = List.copyOf(children);
    }

    public SyntaxToken getIfKeyword() {
        return ifKeyword;
    }

    public ExpressionSyntax getCondition() {
        return condition;
    }

    public StatementSyntax getThenStatement() {
        return thenStatement;
    }

    public ElseClauseSyntax getElseClause() {
        return elseClause;
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
