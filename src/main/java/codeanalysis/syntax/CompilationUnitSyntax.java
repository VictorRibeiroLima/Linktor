package codeanalysis.syntax;

import codeanalysis.syntax.statements.StatementSyntax;

import java.util.Arrays;
import java.util.List;

public class CompilationUnitSyntax extends SyntaxNode {
    private final SyntaxKind kind;
    private final StatementSyntax expression;
    private final SyntaxToken endOfFileToken;

    public CompilationUnitSyntax(StatementSyntax expression, SyntaxToken endOfFileToken) {
        this.kind = SyntaxKind.COMPILATION_UNIT;
        this.expression = expression;
        this.endOfFileToken = endOfFileToken;
    }

    public StatementSyntax getExpression() {
        return expression;
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
        return Arrays.asList(expression, endOfFileToken);
    }
}
