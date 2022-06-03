package codeanalysis.binding.statement.loop;

import codeanalysis.binding.BoundNode;
import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.statement.BoundStatement;

import java.util.List;

public class BoundForStatement extends BoundStatement {
    private final BoundForConditionClause condition;

    private final BoundStatement thenStatement;


    private final List<BoundNode> children;
    private final BoundNodeKind kind;

    public BoundForStatement(BoundForConditionClause condition, BoundStatement thenStatement) {
        this.condition = condition;
        this.thenStatement = thenStatement;
        this.kind = BoundNodeKind.FOR_STATEMENT;
        this.children = List.of(condition, thenStatement);
    }

    @Override
    public List<BoundNode> getChildren() {
        return children;
    }

    public BoundForConditionClause getCondition() {
        return condition;
    }

    public BoundStatement getThenStatement() {
        return thenStatement;
    }

    @Override
    public BoundNodeKind getKind() {
        return kind;
    }
}
