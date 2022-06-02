package codeanalysis.binding.expression.unary;

import codeanalysis.binding.BoundNode;
import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.expression.BoundExpression;

import java.lang.reflect.Type;
import java.util.List;

public class BoundUnaryExpression extends BoundExpression {
    private final BoundUnaryOperator operator;
    private final BoundExpression right;
    private final BoundNodeKind kind;

    private final List<BoundNode> children;
    private final Type type;

    public BoundUnaryExpression(BoundUnaryOperator operator, BoundExpression right) {
        this.operator = operator;
        this.right = right;
        this.kind = BoundNodeKind.UNARY_EXPRESSION;
        this.type = this.operator.getResultType();
        this.children = List.of(right);
    }

    @Override
    public List<BoundNode> getChildren() {
        return children;
    }

    public BoundUnaryOperator getOperator() {
        return operator;
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
