package codeanalysis.binding.statement.jumpto;

import codeanalysis.binding.BoundNode;
import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.statement.BoundStatement;

import java.util.List;

public class BoundJumpToStatement extends BoundStatement {
    private final BoundLabel label;
    private final BoundNodeKind kind;
    private final List<BoundNode> children;

    public BoundJumpToStatement(BoundLabel label) {
        this.label = label;
        this.kind = BoundNodeKind.JUMP_TO_STATEMENT;
        children = List.of();
    }

    public BoundLabel getLabel() {
        return label;
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
