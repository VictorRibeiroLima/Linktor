package codeanalysis.binding.statement.declaration;

import codeanalysis.binding.BoundNode;
import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.statement.BoundStatement;
import codeanalysis.binding.statement.jumpto.BoundLabel;

import java.util.List;

public class BoundLabelDeclarationStatement extends BoundStatement {
    private final BoundLabel label;
    private final BoundNodeKind kind;
    private final List<BoundNode> children;

    public BoundLabelDeclarationStatement(BoundLabel label) {
        this.label = label;
        this.kind = BoundNodeKind.LABEL_DECLARATION_STATEMENT;
        this.children = List.of();
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
