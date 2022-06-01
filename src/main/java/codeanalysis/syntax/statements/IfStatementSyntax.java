package codeanalysis.syntax.statements;

import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;
import codeanalysis.syntax.expression.ExpressionSyntax;

import java.util.Arrays;
import java.util.List;

public class IfStatementSyntax extends StatementSyntax {
    private final SyntaxToken ifWorld;

    private final ExpressionSyntax condition;

    private final StatementSyntax thenStatement;

    private final ElseClauseSyntax elseClause;

    private final SyntaxKind kind;

    private final List<SyntaxNode> children;

    public IfStatementSyntax(SyntaxToken ifWorld, ExpressionSyntax condition, StatementSyntax thenStatement,
                             ElseClauseSyntax elseClause) {
        this.ifWorld = ifWorld;
        this.condition = condition;
        this.thenStatement = thenStatement;
        this.kind = SyntaxKind.IF_STATEMENT;
        this.elseClause = elseClause;
        this.children = Arrays.asList(ifWorld, condition, thenStatement, elseClause);
    }

    public SyntaxToken getIfWorld() {
        return ifWorld;
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
