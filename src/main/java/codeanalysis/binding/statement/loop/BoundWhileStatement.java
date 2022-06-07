package codeanalysis.binding.statement.loop;

import codeanalysis.binding.BoundNode;
import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.binding.statement.BoundStatement;
import codeanalysis.binding.statement.jumpto.BoundLabel;

import java.util.List;

public class BoundWhileStatement extends BoundLoopStatement {
    private final BoundExpression condition;

    private final BoundStatement thenStatement;

    private final BoundNodeKind kind;

    private final List<BoundNode> children;

    public BoundWhileStatement(BoundExpression condition, BoundStatement thenStatement, BoundLabel breakLabel, BoundLabel continueLabel) {
        super(breakLabel, continueLabel);
        this.condition = condition;
        this.thenStatement = thenStatement;
        this.kind = BoundNodeKind.WHILE_STATEMENT;
        this.children = List.of(condition, thenStatement);
    }

    @Override
    public List<BoundNode> getChildren() {
        return children;
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
