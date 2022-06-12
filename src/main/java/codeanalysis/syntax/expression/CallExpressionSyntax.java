package codeanalysis.syntax.expression;

import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;
import codeanalysis.syntax.SyntaxTree;

import java.util.ArrayList;
import java.util.List;

public class CallExpressionSyntax extends ExpressionSyntax {
    private final SyntaxToken identifier;
    private final SyntaxToken open;
    private final SeparatedSyntaxList<ExpressionSyntax> args;
    private final SyntaxToken close;
    private final SyntaxKind kind;
    private final List<SyntaxNode> children;

    public CallExpressionSyntax(SyntaxTree tree, SyntaxToken identifier, SyntaxToken open, SeparatedSyntaxList<ExpressionSyntax> args, SyntaxToken close) {
        super(tree);
        this.identifier = identifier;
        this.open = open;
        this.args = args;
        this.close = close;
        kind = SyntaxKind.CALL_EXPRESSION;
        List<SyntaxNode> children = new ArrayList<>();
        children.add(identifier);
        children.add(open);
        children.addAll(args.getSeparatorAndNodes());
        children.add(close);
        this.children = List.copyOf(children);
    }

    public SeparatedSyntaxList<ExpressionSyntax> getArgs() {
        return args;
    }

    public SyntaxToken getIdentifier() {
        return identifier;
    }

    @Override
    public SyntaxKind getKind() {
        return kind;
    }

    @Override
    public List<SyntaxNode> getChildren() {
        return children;
    }

    public SyntaxToken getOpen() {
        return open;
    }

    public SyntaxToken getClose() {
        return close;
    }
}
