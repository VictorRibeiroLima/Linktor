package codeanalysis.binding.statement.conditional;

import codeanalysis.binding.BoundNode;
import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.binding.statement.BoundStatement;

import java.util.ArrayList;
import java.util.List;

public class BoundIfStatement extends BoundStatement {
    private final BoundExpression condition;

    private final BoundStatement thenStatement;

    private final BoundElseClause elseClause;


    private final List<BoundNode> children;

    private final BoundNodeKind kind;

    public BoundIfStatement(BoundExpression condition, BoundStatement thenStatement, BoundElseClause elseClause) {
        this.condition = condition;
        this.thenStatement = thenStatement;
        this.elseClause = elseClause;
        this.kind = BoundNodeKind.IF_STATEMENT;
        List<BoundNode> children = new ArrayList<>();
        children.add(condition);
        children.add(thenStatement);
        if (elseClause != null)
            children.add(elseClause);
        this.children = List.copyOf(children);
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

    public BoundElseClause getElseClause() {
        return elseClause;
    }

    @Override
    public BoundNodeKind getKind() {
        return kind;
    }
}
