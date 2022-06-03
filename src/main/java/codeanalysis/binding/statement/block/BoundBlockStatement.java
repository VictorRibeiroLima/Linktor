package codeanalysis.binding.statement.block;

import codeanalysis.binding.BoundNode;
import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.statement.BoundStatement;

import java.util.List;

public class BoundBlockStatement extends BoundStatement {
    private final List<BoundStatement> statements;


    private final List<BoundNode> children;

    private final BoundNodeKind kind;

    public BoundBlockStatement(List<BoundStatement> statements) {
        this.statements = List.copyOf(statements);
        this.kind = BoundNodeKind.BLOCK_STATEMENT;
        this.children = List.copyOf(statements);
    }

    @Override
    public List<BoundNode> getChildren() {
        return children;
    }

    public List<BoundStatement> getStatements() {
        return statements;
    }

    @Override
    public BoundNodeKind getKind() {
        return kind;
    }
}
