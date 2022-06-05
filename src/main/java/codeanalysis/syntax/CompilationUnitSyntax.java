package codeanalysis.syntax;

import codeanalysis.syntax.member.MemberSyntax;

import java.util.ArrayList;
import java.util.List;

public class CompilationUnitSyntax extends SyntaxNode {
    private final SyntaxKind kind;
    private final List<MemberSyntax> members;
    private final SyntaxToken endOfFileToken;

    private final List<SyntaxNode> children;

    public CompilationUnitSyntax(List<MemberSyntax> members, SyntaxToken endOfFileToken) {
        this.kind = SyntaxKind.COMPILATION_UNIT;
        this.members = List.copyOf(members);
        this.endOfFileToken = endOfFileToken;
        List<SyntaxNode> children = new ArrayList<>();
        children.addAll(members);
        children.add(endOfFileToken);
        this.children = children;
    }

    public List<MemberSyntax> getMembers() {
        return members;
    }

    public SyntaxToken getEndOfFileToken() {
        return endOfFileToken;
    }

    @Override
    public SyntaxKind getKind() {
        return kind;
    }

    @Override
    public List<SyntaxNode> getChildren() {
        return children;
    }
}
