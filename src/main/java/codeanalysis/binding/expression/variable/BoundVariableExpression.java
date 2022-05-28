package codeanalysis.binding.expression.variable;

import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.symbol.VariableSymbol;

import java.lang.reflect.Type;

public class BoundVariableExpression extends BoundExpression {
    private final VariableSymbol variable;

    private final BoundNodeKind kind;

    private final Type type;

    public BoundVariableExpression(VariableSymbol variable) {
        this.variable = variable;
        this.kind = BoundNodeKind.VARIABLE_EXPRESSION;
        this.type = variable.type();
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
