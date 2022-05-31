package codeanalysis.syntax.statements;

import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;

import java.util.Arrays;
import java.util.List;

public class BlockStatementSyntax extends StatementSyntax {
    private final SyntaxToken openBrace;
    private final List<StatementSyntax> statements;

    private final SyntaxToken closeBrace;

    private final SyntaxKind kind;

    public BlockStatementSyntax(SyntaxToken openBrace, List<StatementSyntax> statements, SyntaxToken closeBrace) {
        this.openBrace = openBrace;
        this.statements = List.copyOf(statements);
        this.closeBrace = closeBrace;
        this.kind = SyntaxKind.BLOCK_STATEMENT;
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
        return Arrays.asList(openBrace, closeBrace);
    }
}
