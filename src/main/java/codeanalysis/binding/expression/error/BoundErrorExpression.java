package codeanalysis.binding.expression.error;

import codeanalysis.binding.BoundNode;
import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.symbol.TypeSymbol;

import java.util.List;

public class BoundErrorExpression extends BoundExpression {
    private final TypeSymbol type = TypeSymbol.ERROR;
    private final BoundNodeKind kind = BoundNodeKind.ERROR_EXPRESSION;

    private final List<BoundNode> children = List.of();

    public BoundErrorExpression() {
    }

    @Override
    public TypeSymbol getType() {
        return type;
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
