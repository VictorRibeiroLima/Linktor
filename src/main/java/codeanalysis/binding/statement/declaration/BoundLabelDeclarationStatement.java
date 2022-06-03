package codeanalysis.binding.statement.declaration;

import codeanalysis.binding.BoundNode;
import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.statement.BoundStatement;
import codeanalysis.symbol.LabelSymbol;

import java.util.List;

public class BoundLabelDeclarationStatement extends BoundStatement {
    private final LabelSymbol label;
    private final BoundNodeKind kind;
    private final List<BoundNode> children;

    public BoundLabelDeclarationStatement(LabelSymbol label) {
        this.label = label;
        this.kind = BoundNodeKind.LABEL_DECLARATION_STATEMENT;
        this.children = List.of();
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
