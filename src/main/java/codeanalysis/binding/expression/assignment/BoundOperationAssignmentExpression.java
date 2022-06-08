package codeanalysis.binding.expression.assignment;

import codeanalysis.binding.BoundNode;
import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.symbol.TypeSymbol;
import codeanalysis.symbol.variable.VariableSymbol;

import java.util.List;

public class BoundOperationAssignmentExpression extends BoundExpression {

    private final VariableSymbol variable;
    private final BoundOperatorAssignmentOperator operator;
    private final BoundExpression boundExpression;
    private final BoundNodeKind kind;
    private final List<BoundNode> children;
    private final TypeSymbol type;

    public BoundOperationAssignmentExpression(VariableSymbol variable, BoundOperatorAssignmentOperator operator, BoundExpression boundExpression) {
        this.variable = variable;
        this.operator = operator;
        this.boundExpression = boundExpression;
        this.kind = BoundNodeKind.OPERATION_ASSIGNMENT_EXPRESSION;
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

    public BoundOperatorAssignmentOperator getOperator() {
        return operator;
    }

    public BoundExpression getBoundExpression() {
        return boundExpression;
    }
}
