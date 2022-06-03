package codeanalysis.binding.statement.expression;

import codeanalysis.binding.BoundNode;
import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.binding.statement.BoundStatement;

import java.util.List;

public class BoundExpressionStatement extends BoundStatement {
    private final BoundExpression expression;


    private final List<BoundNode> children;
    private final BoundNodeKind kind;

    public BoundExpressionStatement(BoundExpression expression) {
        this.expression = expression;
        this.kind = BoundNodeKind.EXPRESSION_STATEMENT;
        this.children = List.of(expression);
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
