package codeanalysis.binding.expression.binary;

import codeanalysis.binding.BoundNode;
import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.expression.BoundExpression;

import java.lang.reflect.Type;
import java.util.List;

public class BoundBinaryExpression extends BoundExpression {
    private final BoundBinaryOperator operator;
    private final BoundExpression left;
    private final BoundExpression right;

    private final List<BoundNode> children;

    private final BoundNodeKind kind;

    private final Type type;

    public BoundBinaryExpression(BoundExpression left, BoundBinaryOperator operator, BoundExpression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
        this.kind = BoundNodeKind.BINARY_EXPRESSION;
        this.type = this.operator.getResultType();
        this.children = List.of(left, right);
    }

    @Override
    public List<BoundNode> getChildren() {
        return children;
    }

    public BoundBinaryOperator getOperator() {
        return operator;
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
