package codeanalysis.syntax.expression;

import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxTree;

public abstract class ExpressionSyntax extends SyntaxNode {
    protected ExpressionSyntax(SyntaxTree tree) {
        super(tree);
    }
}
