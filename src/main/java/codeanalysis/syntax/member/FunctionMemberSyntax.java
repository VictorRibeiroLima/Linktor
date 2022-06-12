package codeanalysis.syntax.member;

import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;
import codeanalysis.syntax.SyntaxTree;
import codeanalysis.syntax.clause.ParameterClauseSyntax;
import codeanalysis.syntax.clause.TypeClauseSyntax;
import codeanalysis.syntax.expression.SeparatedSyntaxList;
import codeanalysis.syntax.statements.BlockStatementSyntax;

import java.util.ArrayList;
import java.util.List;

public class FunctionMemberSyntax extends MemberSyntax {
    private final SyntaxToken functionKeyword;
    private final SyntaxToken identifier;
    private final SyntaxToken open;
    private final SeparatedSyntaxList<ParameterClauseSyntax> params;
    private final SyntaxToken close;
    private final TypeClauseSyntax type;
    private final BlockStatementSyntax body;
    private final SyntaxKind kind;
    private final List<SyntaxNode> children;


    public FunctionMemberSyntax(SyntaxTree tree,
                                SyntaxToken functionKeyword,
                                SyntaxToken identifier,
                                SyntaxToken open,
                                SeparatedSyntaxList<ParameterClauseSyntax> params,
                                SyntaxToken close,
                                TypeClauseSyntax type, BlockStatementSyntax body) {
        super(tree);
        this.functionKeyword = functionKeyword;
        this.identifier = identifier;
        this.open = open;
        this.params = params;
        this.close = close;
        this.type = type;
        this.body = body;
        this.kind = SyntaxKind.FUNCTION_MEMBER;
        List<SyntaxNode> children = new ArrayList<>();
        children.add(functionKeyword);
        children.add(identifier);
        children.add(open);
        params.iterator().forEachRemaining(children::add);
        children.add(close);
        if (type != null)
            children.add(type);
        children.add(body);
        this.children = List.copyOf(children);
    }

    public SyntaxToken getFunctionKeyword() {
        return functionKeyword;
    }

    public SyntaxToken getIdentifier() {
        return identifier;
    }

    public SyntaxToken getOpen() {
        return open;
    }

    public SeparatedSyntaxList<ParameterClauseSyntax> getParams() {
        return params;
    }

    public SyntaxToken getClose() {
        return close;
    }

    public TypeClauseSyntax getType() {
        return type;
    }

    public BlockStatementSyntax getBody() {
        return body;
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
