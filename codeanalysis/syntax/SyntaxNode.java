package codeanalysis.syntax;

import java.util.List;

public abstract class SyntaxNode {
    public abstract SyntaxType getType();

    public abstract List<SyntaxNode> getChildren();

}
