package codeanalysis.syntax;

import codeanalysis.diagnostics.TextSpan;

import java.util.List;

public abstract class SyntaxNode {
    public abstract SyntaxKind getKind();

    public abstract List<SyntaxNode> getChildren();

    public TextSpan getSpan() {
        TextSpan first = null;
        TextSpan last = null;
        if (!this.getChildren().isEmpty()) {
            first = this.getChildren().get(0).getSpan();
            last = this.getChildren().get(this.getChildren().size() - 1).getSpan();
        }
        return TextSpan.fromBounds(first.start(), last.end());
    }

}
