package codeanalysis.syntax.member;

import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.statements.StatementSyntax;

import java.util.List;

public class GlobalMemberSyntax extends MemberSyntax {
    private final StatementSyntax statement;
    private final SyntaxKind kind;
    private final List<SyntaxNode> children;

    public GlobalMemberSyntax(StatementSyntax statement) {
        this.statement = statement;
        this.kind = SyntaxKind.GLOBAL_MEMBER;
        this.children = List.of(statement);
    }

    public StatementSyntax getStatement() {
        return statement;
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
