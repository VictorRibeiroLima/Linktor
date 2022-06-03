package codeanalysis.binding.statement.jumpto;

import codeanalysis.binding.BoundNode;
import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.statement.BoundStatement;
import codeanalysis.symbol.LabelSymbol;

import java.util.List;

public class BoundJumpToStatement extends BoundStatement {
    private final LabelSymbol label;
    private final BoundNodeKind kind;
    private final List<BoundNode> children;

    public BoundJumpToStatement(LabelSymbol label) {
        this.label = label;
        this.kind = BoundNodeKind.JUMP_TO_STATEMENT;
        children = List.of();
    }

    public LabelSymbol getLabel() {
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
