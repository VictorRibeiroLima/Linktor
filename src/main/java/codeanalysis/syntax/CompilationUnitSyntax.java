package codeanalysis.syntax;

import codeanalysis.syntax.statements.StatementSyntax;

import java.util.Arrays;
import java.util.List;

public class CompilationUnitSyntax extends SyntaxNode {
    private final SyntaxKind kind;
    private final StatementSyntax statement;
    private final SyntaxToken endOfFileToken;

    public CompilationUnitSyntax(StatementSyntax statement, SyntaxToken endOfFileToken) {
        this.kind = SyntaxKind.COMPILATION_UNIT;
        this.statement = statement;
        this.endOfFileToken = endOfFileToken;
    }

    public StatementSyntax getStatement() {
        return statement;
    }

    public SyntaxToken getEndOfFileToken() {
        return endOfFileToken;
    }

    @Override
    public SyntaxKind getKind() {
        return kind;
    }

    @Override
    public List<SyntaxNode> getChildren() {
        return Arrays.asList(statement, endOfFileToken);
    }
}
