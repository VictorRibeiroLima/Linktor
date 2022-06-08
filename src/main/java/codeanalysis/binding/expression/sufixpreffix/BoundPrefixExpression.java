package codeanalysis.binding.expression.sufixpreffix;

import codeanalysis.binding.BoundNode;
import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.symbol.TypeSymbol;
import codeanalysis.symbol.variable.VariableSymbol;

import java.util.List;

public class BoundPrefixExpression extends BoundExpression {
    private final BoundPrefixSuffixOperator operator;
    private final VariableSymbol right;
    private final BoundNodeKind kind;

    private final List<BoundNode> children;
    private final TypeSymbol type;

    public BoundPrefixExpression(BoundPrefixSuffixOperator operator, VariableSymbol right) {
        this.right = right;
        this.operator = operator;
        this.kind = BoundNodeKind.PREFIX_EXPRESSION;
        this.type = this.operator.getResultType();
        this.children = List.of();
    }

    @Override
    public List<BoundNode> getChildren() {
        return children;
    }

    public BoundPrefixSuffixOperator getOperator() {
        return operator;
    }

    public VariableSymbol getRight() {
        return right;
    }

    @Override
    public BoundNodeKind getKind() {
        return this.kind;
    }

    @Override
    public TypeSymbol getType() {
        return type;
    }
}
