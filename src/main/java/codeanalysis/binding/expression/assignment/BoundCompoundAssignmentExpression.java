package codeanalysis.binding.expression.assignment;

import codeanalysis.binding.BoundNode;
import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.binding.expression.binary.BoundBinaryOperator;
import codeanalysis.symbol.TypeSymbol;
import codeanalysis.symbol.variable.VariableSymbol;

import java.util.List;

public class BoundCompoundAssignmentExpression extends BoundExpression {

    private final VariableSymbol variable;
    private final BoundBinaryOperator operator;
    private final BoundExpression boundExpression;
    private final BoundNodeKind kind;
    private final List<BoundNode> children;
    private final TypeSymbol type;

    public BoundCompoundAssignmentExpression(VariableSymbol variable, BoundBinaryOperator operator, BoundExpression boundExpression) {
        this.variable = variable;
        this.operator = operator;
        this.boundExpression = boundExpression;
        this.kind = BoundNodeKind.COMPOUND_ASSIGNMENT_EXPRESSION;
        this.type = boundExpression.getType();
        this.children = List.of(boundExpression);
    }

    @Override
    public BoundNodeKind getKind() {
        return this.kind;
    }

    @Override
    public List<BoundNode> getChildren() {
        return children;
    }

    @Override
    public TypeSymbol getType() {
        return this.type;
    }

    public VariableSymbol getVariable() {
        return variable;
    }

    public BoundBinaryOperator getOperator() {
        return operator;
    }

    public BoundExpression getBoundExpression() {
        return boundExpression;
    }
}
