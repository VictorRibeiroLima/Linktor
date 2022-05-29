package codeanalysis.binding.expression.assignment;

import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.symbol.VariableSymbol;

import java.lang.reflect.Type;

public class BoundAssignmentExpression extends BoundExpression {

    private final VariableSymbol variable;
    private final BoundExpression boundExpression;
    private final BoundNodeKind kind;
    private final Type type;

    public BoundAssignmentExpression(VariableSymbol variable, BoundExpression boundExpression) {
        this.variable = variable;
        this.boundExpression = boundExpression;
        this.kind = BoundNodeKind.ASSIGNMENT_EXPRESSION;
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

    public BoundExpression getBoundExpression() {
        return boundExpression;
    }
}
