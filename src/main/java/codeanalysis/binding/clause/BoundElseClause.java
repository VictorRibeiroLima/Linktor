package codeanalysis.binding.clause;

import codeanalysis.binding.BoundNode;
import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.statement.BoundStatement;

public class BoundElseClause extends BoundNode {
    private final BoundStatement thenStatement;

    private final BoundNodeKind kind;

    public BoundElseClause(BoundStatement thenStatement) {
        this.thenStatement = thenStatement;
        this.kind = BoundNodeKind.ELSE_CLAUSE;
    }

    public BoundStatement getThenStatement() {
        return thenStatement;
    }

    @Override
    public BoundNodeKind getKind() {
        return kind;
    }


}
