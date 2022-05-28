package src.codeanalysis.binding.expression.variable;

import src.codeanalysis.binding.BoundNodeKind;
import src.codeanalysis.binding.expression.BoundExpression;

import java.lang.reflect.Type;

public class BoundVariableExpression extends BoundExpression {
    private final String name;

    private final BoundNodeKind kind;

    private final Type type;

    public BoundVariableExpression(String name, Type type) {
        this.name = name;
        this.kind = BoundNodeKind.VARIABLE_EXPRESSION;
        this.type = type;
    }

    @Override
    public BoundNodeKind getKind() {
        return null;
    }

    @Override
    public Type getType() {
        return null;
    }
}
