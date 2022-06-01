package codeanalysis.binding.clause;

import codeanalysis.binding.BoundNode;
import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.expression.BoundExpression;

public class BoundForConditionClause extends BoundNode {
    private final BoundNode variable;
    private final BoundExpression conditionExpression;
    private final BoundExpression incrementExpression;

    private final BoundNodeKind kind;

    public BoundForConditionClause(BoundNode variable, BoundExpression conditionExpression, BoundExpression incrementExpression) {
        this.variable = variable;
        this.conditionExpression = conditionExpression;
        this.incrementExpression = incrementExpression;
        this.kind = BoundNodeKind.FOR_CONDITION_CLAUSE;
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
