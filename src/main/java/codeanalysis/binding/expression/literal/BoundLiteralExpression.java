package codeanalysis.binding.expression.literal;

import codeanalysis.binding.BoundNode;
import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.symbol.TypeSymbol;

import java.util.List;

public class BoundLiteralExpression extends BoundExpression {
    private final Object value;
    private final BoundNodeKind kind;

    private final List<BoundNode> children;
    private final TypeSymbol type;


    public BoundLiteralExpression(Object value) {
        Class<?> clazz = value.getClass();
        if (clazz.equals(Integer.class))
            this.type = TypeSymbol.INTEGER;
        else if (clazz.equals(String.class))
            this.type = TypeSymbol.STRING;
        else if (clazz.equals(Boolean.class))
            this.type = TypeSymbol.BOOLEAN;
        else
            throw new RuntimeException("Unexpected literal" + value + "of type:" + clazz);

        this.value = value;
        this.kind = BoundNodeKind.LITERAL_EXPRESSION;
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
    public TypeSymbol getType() {
        return type;
    }
}
