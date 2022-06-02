package codeanalysis.binding.clause;

import codeanalysis.binding.BoundNode;
import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.statement.BoundStatement;

import java.util.List;

public class BoundElseClause extends BoundNode {
    private final BoundStatement thenStatement;

    private final BoundNodeKind kind;


    private final List<BoundNode> children;

    public BoundElseClause(BoundStatement thenStatement) {
        this.thenStatement = thenStatement;
        this.kind = BoundNodeKind.ELSE_CLAUSE;
        this.children = List.of(thenStatement);
    }

    @Override
    public List<BoundNode> getChildren() {
        return children;
    }

    public BoundStatement getThenStatement() {
        return thenStatement;
    }

    @Override
    public BoundNodeKind getKind() {
        return kind;
    }


}
