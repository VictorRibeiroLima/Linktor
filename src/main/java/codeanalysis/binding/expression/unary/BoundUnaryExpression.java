package src.codeanalysis.binding.expression.unary;

import src.codeanalysis.binding.BoundNodeKind;
import src.codeanalysis.binding.expression.BoundExpression;

import java.lang.reflect.Type;

public class BoundUnaryExpression extends BoundExpression {
    private final BoundUnaryOperator operator;
    private final BoundExpression right;
    private final BoundNodeKind kind;

    private final Type type;

    public BoundUnaryExpression(BoundUnaryOperator operator, BoundExpression right) {
        this.operator = operator;
        this.right = right;
        this.kind = BoundNodeKind.UNARY_EXPRESSION;
        this.type = this.operator.getResultType();
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
