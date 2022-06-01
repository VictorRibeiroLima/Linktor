package codeanalysis.syntax.clause;

import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;
import codeanalysis.syntax.statements.StatementSyntax;

import java.util.Arrays;
import java.util.List;

public class ElseClauseSyntax extends SyntaxNode {
    private final SyntaxToken elseKeyword;

    private final StatementSyntax thenStatement;

    private final SyntaxKind kind;

    private final List<SyntaxNode> children;

    public ElseClauseSyntax(SyntaxToken elseKeyword, StatementSyntax thenStatement) {
        this.elseKeyword = elseKeyword;
        this.thenStatement = thenStatement;
        this.kind = SyntaxKind.ElSE_CLAUSE;
        this.children = Arrays.asList(elseKeyword, thenStatement);
    }

    public SyntaxToken getElseKeyword() {
        return elseKeyword;
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
