package codeanalysis.binding.statement.loop;

import codeanalysis.binding.BoundNode;
import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.expression.BoundExpression;

import java.util.List;

public class BoundForConditionClause extends BoundNode {
    private final BoundNode variable;
    private final BoundExpression conditionExpression;
    private final BoundExpression incrementExpression;


    private final List<BoundNode> children;

    private final BoundNodeKind kind;

    public BoundForConditionClause(BoundNode variable, BoundExpression conditionExpression, BoundExpression incrementExpression) {
        this.variable = variable;
        this.conditionExpression = conditionExpression;
        this.incrementExpression = incrementExpression;
        this.kind = BoundNodeKind.FOR_CONDITION_CLAUSE;
        this.children = List.of(variable, conditionExpression, incrementExpression);
    }

    @Override
    public List<BoundNode> getChildren() {
        return children;
    }

    public BoundNode getVariable() {
        return variable;
    }

    public BoundExpression getConditionExpression() {
        return conditionExpression;
    }

    public BoundExpression getIncrementExpression() {
        return incrementExpression;
    }

    @Override
    public BoundNodeKind getKind() {
        return kind;
    }


}
