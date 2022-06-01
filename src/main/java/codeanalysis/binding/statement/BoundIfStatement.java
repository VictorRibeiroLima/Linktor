package codeanalysis.binding.statement;

import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.expression.BoundExpression;

public class BoundIfStatement extends BoundStatement {
    private final BoundExpression condition;

    private final BoundStatement thenStatement;

    private final BoundElseClause elseClause;

    private final BoundNodeKind kind;

    public BoundIfStatement(BoundExpression condition, BoundStatement thenStatement, BoundElseClause elseClause) {
        this.condition = condition;
        this.thenStatement = thenStatement;
        this.elseClause = elseClause;
        this.kind = BoundNodeKind.IF_STATEMENT;
    }

    public BoundExpression getCondition() {
        return condition;
    }

    public BoundStatement getThenStatement() {
        return thenStatement;
    }

    public BoundElseClause getElseClause() {
        return elseClause;
    }

    @Override
    public BoundNodeKind getKind() {
        return kind;
    }
}
