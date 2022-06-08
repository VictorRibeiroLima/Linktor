package codeanalysis.syntax.statements;

import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;
import codeanalysis.syntax.clause.TypeClauseSyntax;
import codeanalysis.syntax.expression.ExpressionSyntax;

import java.util.ArrayList;
import java.util.List;

public class VariableDeclarationStatementSyntax extends StatementSyntax {
    private final SyntaxToken keyword;

    private final SyntaxToken identifier;

    private final TypeClauseSyntax type;

    private final SyntaxToken equals;

    private final ExpressionSyntax initializer;

    private final SyntaxKind kind;

    private final List<SyntaxNode> children;

    public VariableDeclarationStatementSyntax(SyntaxToken keyword, SyntaxToken identifier, TypeClauseSyntax type, SyntaxToken equals, ExpressionSyntax initializer) {
        this.keyword = keyword;
        this.identifier = identifier;
        this.type = type;
        this.equals = equals;
        this.initializer = initializer;
        this.kind = SyntaxKind.VARIABLE_DECLARATION_STATEMENT;
        List<SyntaxNode> children = new ArrayList<>();
        children.add(keyword);
        children.add(identifier);
        if (type != null)
            children.add(type);
        if (equals != null) {
            children.add(equals);
            children.add(initializer);
        }
        this.children = List.copyOf(children);
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

    public TypeClauseSyntax getType() {
        return type;
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
