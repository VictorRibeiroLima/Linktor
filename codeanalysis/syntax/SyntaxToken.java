package codeanalysis.syntax;

import java.util.ArrayList;
import java.util.List;

public class SyntaxToken extends SyntaxNode {
    private final SyntaxType type;
    private final int position;
    private final String text;
    private final Object value;

    public SyntaxToken(SyntaxType type, int position, String text, Object value) {
        this.type = type;
        this.position = position;
        this.text = text;
        this.value = value;
    }

    public SyntaxType getType() {
        return type;
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
