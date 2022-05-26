package codeanalysis.binding.expression.binary;

import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.expression.BoundExpression;

import java.lang.reflect.Type;

public class BoundBinaryExpression extends BoundExpression {
    private final BoundBinaryOperatorKind operatorKind;
    private final BoundExpression left;
    private final BoundExpression right;

    private final BoundNodeKind kind;

    private final Type type;

    public BoundBinaryExpression(BoundExpression left, BoundBinaryOperatorKind operatorKind, BoundExpression right) {
        this.left = left;
        this.operatorKind = operatorKind;
        this.right = right;
        this.kind = BoundNodeKind.BINARY_EXPRESSION;
        this.type = this.left.getType();
    }

    public BoundBinaryOperatorKind getOperatorKind() {
        return operatorKind;
    }

    public BoundExpression getLeft() {
        return left;
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
        return this.type;
    }
}
