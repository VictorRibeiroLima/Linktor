package codeanalysis.syntax.member;

import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxTree;

public abstract class MemberSyntax extends SyntaxNode {
    protected MemberSyntax(SyntaxTree tree) {
        super(tree);
    }
}
