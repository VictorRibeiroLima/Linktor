package codeanalysis.binding.statement.jumpto;

import codeanalysis.binding.BoundNode;
import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.binding.statement.BoundStatement;
import codeanalysis.symbol.LabelSymbol;

import java.util.List;

public class BoundConditionalJumpToStatement extends BoundStatement {
    private final LabelSymbol label;
    private final BoundExpression condition;
    private final boolean jumpIfFalse;
    private final BoundNodeKind kind;
    private final List<BoundNode> children;

    public BoundConditionalJumpToStatement(LabelSymbol label, BoundExpression condition) {
        this(label, condition, false);
    }

    public BoundConditionalJumpToStatement(LabelSymbol label, BoundExpression condition, boolean jumpIfFalse) {
        this.label = label;
        this.condition = condition;
        this.jumpIfFalse = jumpIfFalse;
        this.kind = BoundNodeKind.CONDITIONAL_JUMP_TO_STATEMENT;
        this.children = List.of(condition);
    }

    public LabelSymbol getLabel() {
        return label;
    }

    public BoundExpression getCondition() {
        return condition;
    }

    public boolean isJumpIfFalse() {
        return jumpIfFalse;
    }

    @Override
    public BoundNodeKind getKind() {
        return kind;
    }

    @Override
    public List<BoundNode> getChildren() {
        return children;
    }
}
