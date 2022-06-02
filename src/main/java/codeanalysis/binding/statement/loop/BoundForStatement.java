package codeanalysis.binding.statement.loop;

import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.statement.BoundStatement;

public class BoundForStatement extends BoundStatement {
    private final BoundForConditionClause condition;

    private final BoundStatement thenStatement;

    private final BoundNodeKind kind;

    public BoundForStatement(BoundForConditionClause condition, BoundStatement thenStatement) {
        this.condition = condition;
        this.thenStatement = thenStatement;
        this.kind = BoundNodeKind.FOR_STATEMENT;
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
