package codeanalysis.binding.statement;

import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.expression.BoundExpression;

public class BoundWhileStatement extends BoundStatement {
    private final BoundExpression condition;

    private final BoundStatement thenStatement;

    private final BoundNodeKind kind;

    public BoundWhileStatement(BoundExpression condition, BoundStatement thenStatement) {
        this.condition = condition;
        this.thenStatement = thenStatement;
        this.kind = BoundNodeKind.WHILE_STATEMENT;
    }

    public BoundExpression getCondition() {
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
