package codeanalysis.syntax.statements;

import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;

import java.util.List;

public class ContinueStatementSyntax extends StatementSyntax {
    private final SyntaxToken keyword;
    private final SyntaxKind kind;
    private final List<SyntaxNode> children;


    public ContinueStatementSyntax(SyntaxToken continueKeyword) {
        this.keyword = continueKeyword;
        this.kind = SyntaxKind.CONTINUE_STATEMENT;
        this.children = List.of(continueKeyword);
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
