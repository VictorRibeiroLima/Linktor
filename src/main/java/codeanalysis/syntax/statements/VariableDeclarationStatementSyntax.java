package codeanalysis.syntax.statements;

import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;
import codeanalysis.syntax.expression.ExpressionSyntax;

import java.util.Arrays;
import java.util.List;

public class VariableDeclarationStatementSyntax extends StatementSyntax {
    private final SyntaxToken keyword;

    private final SyntaxToken identifier;

    private final SyntaxToken equals;

    private final ExpressionSyntax initializer;

    private final SyntaxKind kind;

    public VariableDeclarationStatementSyntax(SyntaxToken keyword, SyntaxToken identifier, SyntaxToken equals, ExpressionSyntax initializer) {
        this.keyword = keyword;
        this.identifier = identifier;
        this.equals = equals;
        this.initializer = initializer;
        this.kind = SyntaxKind.VARIABLE_DECLARATION_STATEMENT;
    }

    public SyntaxToken getKeyword() {
        return keyword;
    }

    public SyntaxToken getIdentifier() {
        return identifier;
    }

    public SyntaxToken getEquals() {
        return equals;
    }

    public ExpressionSyntax getInitializer() {
        return initializer;
    }

    @Override
    public SyntaxKind getKind() {
        return kind;
    }

    @Override
    public List<SyntaxNode> getChildren() {
        return Arrays.asList(keyword, identifier, equals, initializer);
    }
}
