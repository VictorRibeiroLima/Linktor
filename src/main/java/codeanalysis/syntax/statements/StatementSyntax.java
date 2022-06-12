package codeanalysis.syntax.statements;

import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxTree;

public abstract class StatementSyntax extends SyntaxNode {
    protected StatementSyntax(SyntaxTree tree) {
        super(tree);
    }
}
