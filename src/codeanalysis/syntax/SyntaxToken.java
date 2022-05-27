package src.codeanalysis.syntax;

import java.util.ArrayList;
import java.util.List;

public class SyntaxToken extends SyntaxNode {
    private final SyntaxKind kind;
    private final int position;
    private final String text;
    private final Object value;

    public SyntaxToken(SyntaxKind kind, int position, String text, Object value) {
        this.kind = kind;
        this.position = position;
        this.text = text;
        this.value = value;
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

    public List<SyntaxNode> getChildren() {
        return new ArrayList<>();
    }
}
