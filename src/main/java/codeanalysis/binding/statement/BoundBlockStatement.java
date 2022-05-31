package codeanalysis.binding.statement;

import codeanalysis.binding.BoundNodeKind;

import java.util.List;

public class BoundBlockStatement extends BoundStatement {
    private final List<BoundStatement> statements;

    private final BoundNodeKind kind;

    public BoundBlockStatement(List<BoundStatement> statements) {
        this.statements = List.copyOf(statements);
        this.kind = BoundNodeKind.BLOCK_STATEMENT;
    }

    public List<BoundStatement> getStatements() {
        return statements;
    }

    @Override
    public BoundNodeKind getKind() {
        return kind;
    }
}
