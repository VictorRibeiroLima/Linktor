package codeanalysis.binding.expression.literal;

import codeanalysis.binding.BoundNode;
import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.expression.BoundExpression;

import java.lang.reflect.Type;
import java.util.List;

public class BoundLiteralExpression extends BoundExpression {
    private final Object value;
    private final BoundNodeKind kind;

    private final List<BoundNode> children;
    private final Type type;


    public BoundLiteralExpression(Object value) {
        this.value = value;
        this.kind = BoundNodeKind.LITERAL_EXPRESSION;
        this.type = value.getClass();
        this.children = List.of();
    }

    @Override
    public List<BoundNode> getChildren() {
        return children;
    }

    @Override
    public BoundNodeKind getKind() {
        return this.kind;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public Type getType() {
        return type;
    }
}
