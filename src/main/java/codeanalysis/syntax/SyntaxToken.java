package codeanalysis.syntax;

import codeanalysis.source.TextSpan;

import java.util.List;

public class SyntaxToken extends SyntaxNode {
    private final SyntaxKind kind;
    private final int position;
    private final String text;
    private final Object value;

    private final TextSpan span;
    private final List<SyntaxNode> children;

    public SyntaxToken(SyntaxTree tree, SyntaxKind kind, int position, String text, Object value) {
        super(tree);
        this.kind = kind;
        this.position = position;
        this.text = text;
        this.value = value;
        int length = text != null ? text.length() : 0;
        span = new TextSpan(position, length);
        children = List.of();
    }

    public SyntaxKind getKind() {
        return kind;
    }

    public int getPosition() {
        return position;
    }

    public String getText() {
        return text;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public TextSpan getSpan() {
        return span;
    }

    public List<SyntaxNode> getChildren() {
        return children;
    }

    public boolean isMissing() {
        return text == null;
    }

}
