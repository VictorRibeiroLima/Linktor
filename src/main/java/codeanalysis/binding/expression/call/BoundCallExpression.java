package codeanalysis.binding.expression.call;

import codeanalysis.binding.BoundNode;
import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.symbol.FunctionSymbol;
import codeanalysis.symbol.TypeSymbol;

import java.util.List;

public class BoundCallExpression extends BoundExpression {
    private final FunctionSymbol function;
    private final List<BoundExpression> args;
    private final BoundNodeKind kind;
    private final TypeSymbol type;
    private final List<BoundNode> children;

    public BoundCallExpression(FunctionSymbol function, List<BoundExpression> args) {
        this.function = function;
        this.args = args;
        this.type = function.getType();
        this.kind = BoundNodeKind.CALL_EXPRESSION;
        this.children = List.copyOf(args);
    }

    public FunctionSymbol getFunction() {
        return function;
    }

    public List<BoundExpression> getArgs() {
        return args;
    }

    @Override
    public BoundNodeKind getKind() {
        return kind;
    }

    @Override
    public TypeSymbol getType() {
        return type;
    }

    @Override
    public List<BoundNode> getChildren() {
        return children;
    }
}
