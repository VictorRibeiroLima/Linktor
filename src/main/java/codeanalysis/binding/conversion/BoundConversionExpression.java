package codeanalysis.binding.conversion;

import codeanalysis.binding.BoundNode;
import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.symbol.TypeSymbol;

import java.util.List;

public class BoundConversionExpression extends BoundExpression {
    private final TypeSymbol type;
    private final BoundExpression expression;

    private final BoundNodeKind kind;

    private final List<BoundNode> children;

    public BoundConversionExpression(TypeSymbol type, BoundExpression expression) {
        this.type = type;
        this.expression = expression;
        this.kind = BoundNodeKind.CONVERSION_EXPRESSION;
        this.children = List.of(expression);
    }

    @Override
    public TypeSymbol getType() {
        return type;
    }

    public BoundExpression getExpression() {
        return expression;
    }

    @Override
    public BoundNodeKind getKind() {
        return kind;
    }

    @Override
    public List<BoundNode> getChildren() {
        return children;
    }
}
