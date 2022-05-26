package codeanalysis.binding.expression.unary;

import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.expression.BoundExpression;

import java.lang.reflect.Type;

public class BoundUnaryExpression extends BoundExpression {
    private final BoundUnaryOperatorKind operatorKind;
    private final BoundExpression right;
    private final BoundNodeKind kind;

    private final Type type;

    public BoundUnaryExpression(BoundUnaryOperatorKind operatorKind, BoundExpression right) {
        this.operatorKind = operatorKind;
        this.right = right;
        this.kind = BoundNodeKind.UNARY_EXPRESSION;
        this.type = this.right.getType();
    }

    public BoundUnaryOperatorKind getOperatorKind() {
        return operatorKind;
    }

    public BoundExpression getRight() {
        return right;
    }

    @Override
    public BoundNodeKind getKind() {
        return this.kind;
    }

    @Override
    public Type getType() {
        return type;
    }
}
