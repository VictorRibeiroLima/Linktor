package codeanalysis.syntax.statements;

import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;
import codeanalysis.syntax.SyntaxTree;

import java.util.ArrayList;
import java.util.List;

public class BlockStatementSyntax extends StatementSyntax {
    private final SyntaxToken openBrace;
    private final List<StatementSyntax> statements;

    private final SyntaxToken closeBrace;

    private final SyntaxKind kind;

    private final List<SyntaxNode> children;

    public BlockStatementSyntax(SyntaxTree tree, SyntaxToken openBrace, List<StatementSyntax> statements, SyntaxToken closeBrace) {
        super(tree);
        this.openBrace = openBrace;
        this.statements = List.copyOf(statements);
        this.closeBrace = closeBrace;
        this.kind = SyntaxKind.BLOCK_STATEMENT;
        ArrayList<SyntaxNode> children = new ArrayList<>();
        children.add(openBrace);
        children.addAll(statements);
        children.add(closeBrace);
        this.children = List.copyOf(children);
    }

    public List<StatementSyntax> getStatements() {
        return statements;
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
