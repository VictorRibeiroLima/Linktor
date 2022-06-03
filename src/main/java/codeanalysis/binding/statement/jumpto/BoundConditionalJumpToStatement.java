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
    private final boolean jumpIfTrue;
    private final BoundNodeKind kind;
    private final List<BoundNode> children;

    public BoundConditionalJumpToStatement(LabelSymbol label, BoundExpression condition) {
        this(label, condition, true);
    }

    public BoundConditionalJumpToStatement(LabelSymbol label, BoundExpression condition, boolean jumpIfTrue) {
        this.label = label;
        this.condition = condition;
        this.jumpIfTrue = jumpIfTrue;
        this.kind = BoundNodeKind.CONDITIONAL_JUMP_TO_STATEMENT;
        this.children = List.of(condition);
    }

    public LabelSymbol getLabel() {
        return label;
    }

    public BoundExpression getCondition() {
        return condition;
    }

    public boolean isJumpIfTrue() {
        return jumpIfTrue;
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
