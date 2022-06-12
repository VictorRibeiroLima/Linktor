package codeanalysis.syntax.statements;

import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;
import codeanalysis.syntax.SyntaxTree;

import java.util.List;

public class BreakStatementSyntax extends StatementSyntax {
    private final SyntaxToken keyword;
    private final SyntaxKind kind;
    private final List<SyntaxNode> children;


    public BreakStatementSyntax(SyntaxTree tree, SyntaxToken breakKeyword) {
        super(tree);
        this.keyword = breakKeyword;
        this.kind = SyntaxKind.BREAK_STATEMENT;
        this.children = List.of(breakKeyword);
    }

    public SyntaxToken getKeyword() {
        return keyword;
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
