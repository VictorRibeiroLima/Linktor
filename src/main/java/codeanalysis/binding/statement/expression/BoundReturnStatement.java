package codeanalysis.binding.statement.expression;

import codeanalysis.binding.BoundNode;
import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.binding.statement.BoundStatement;

import java.util.List;

public class BoundReturnStatement extends BoundStatement {
    private final BoundExpression expression;


    private final List<BoundNode> children;
    private final BoundNodeKind kind;

    public BoundReturnStatement(BoundExpression expression) {
        this.expression = expression;
        this.kind = BoundNodeKind.RETURN_STATEMENT;
        if (expression != null)
            this.children = List.of(expression);
        else
            this.children = List.of();
    }

    @Override
    public List<BoundNode> getChildren() {
        return children;
    }

    public BoundExpression getExpression() {
        return expression;
    }

    @Override
    public BoundNodeKind getKind() {
        return kind;
    }
}
