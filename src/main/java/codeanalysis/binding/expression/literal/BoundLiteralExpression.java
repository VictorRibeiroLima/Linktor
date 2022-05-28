package src.codeanalysis.binding.expression.literal;

import src.codeanalysis.binding.BoundNodeKind;
import src.codeanalysis.binding.expression.BoundExpression;

import java.lang.reflect.Type;

public class BoundLiteralExpression extends BoundExpression {
    private final Object value;
    private final BoundNodeKind kind;
    private final Type type;


    public BoundLiteralExpression(Object value) {
        this.value = value;
        this.kind = BoundNodeKind.LITERAL_EXPRESSION;
        this.type = value.getClass();
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
