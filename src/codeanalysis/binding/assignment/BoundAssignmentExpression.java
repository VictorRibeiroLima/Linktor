package src.codeanalysis.binding.assignment;

import src.codeanalysis.binding.BoundNodeKind;
import src.codeanalysis.binding.expression.BoundExpression;

import java.lang.reflect.Type;

public class BoundAssignmentExpression extends BoundExpression {

    private final String name;
    private final BoundExpression boundExpression;
    private final BoundNodeKind kind;
    private final Type type;

    public BoundAssignmentExpression(String name, BoundExpression boundExpression) {
        this.name = name;
        this.boundExpression = boundExpression;
        this.kind = BoundNodeKind.ASSIGNMENT_EXPRESSION;
        this.type = boundExpression.getType();
    }

    @Override
    public BoundNodeKind getKind() {
        return this.kind;
    }

    @Override
    public Type getType() {
        return this.type;
    }

    public String getName() {
        return name;
    }

    public BoundExpression getBoundExpression() {
        return boundExpression;
    }
}
