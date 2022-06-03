package codeanalysis.binding.expression.variable;

import codeanalysis.binding.BoundNode;
import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.symbol.VariableSymbol;

import java.lang.reflect.Type;
import java.util.List;

public class BoundVariableExpression extends BoundExpression {
    private final VariableSymbol variable;

    private final BoundNodeKind kind;

    private final Type type;

    private final List<BoundNode> children;

    public BoundVariableExpression(VariableSymbol variable) {
        this.variable = variable;
        this.kind = BoundNodeKind.VARIABLE_EXPRESSION;
        this.type = variable.type();
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

    @Override
    public Type getType() {
        return this.type;
    }

    public VariableSymbol getVariable() {
        return variable;
    }
}
