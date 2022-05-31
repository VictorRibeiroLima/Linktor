package codeanalysis.binding.statement;

import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.expression.BoundExpression;

public class BoundExpressionStatement extends BoundStatement {
    private final BoundExpression expression;

    private final BoundNodeKind kind;

    public BoundExpressionStatement(BoundExpression expression) {
        this.expression = expression;
        this.kind = BoundNodeKind.EXPRESSION_STATEMENT;
    }

    public BoundExpression getExpression() {
        return expression;
    }

    @Override
    public BoundNodeKind getKind() {
        return kind;
    }
}
