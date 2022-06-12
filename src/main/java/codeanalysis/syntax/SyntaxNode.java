package codeanalysis.syntax;

import codeanalysis.source.TextLocation;
import codeanalysis.source.TextSpan;
import util.ConsoleColors;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public abstract class SyntaxNode {

    private final SyntaxTree tree;

    protected SyntaxNode(SyntaxTree tree) {
        this.tree = tree;
    }

    public abstract SyntaxKind getKind();

    public abstract List<SyntaxNode> getChildren();

    public SyntaxTree getTree() {
        return tree;
    }

    public TextSpan getSpan() {
        TextSpan first = null;
        TextSpan last = null;
        if (!this.getChildren().isEmpty()) {
            first = this.getChildren().get(0).getSpan();
            last = this.getChildren().get(this.getChildren().size() - 1).getSpan();
        }
        return TextSpan.fromBounds(first.start(), last.end());
    }

    public TextLocation getLocation() {
        return new TextLocation(tree.getText(), getSpan());
    }

    public SyntaxToken getLastToken() {
        if (this instanceof SyntaxToken t)
            return t;

        return this.getChildren().get(this.getChildren().size() - 1).getLastToken();
    }

    public void writeTo(PrintWriter out) {
        printTree(out, this);
    }

    @Override
    public String toString() {
        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);
        printTree(out, this);
        return writer.toString();
    }


    private void printTree(PrintWriter out, SyntaxNode node) {
        printTree(out, node, "", false);
    }

    private void printTree(PrintWriter out, SyntaxNode node, String indent, boolean isLast) {
        String color;
        String kindString = node.getKind().toString();
        if (kindString.contains("UNIT"))
            color = ConsoleColors.WHITE_BRIGHT;
        else if (kindString.contains("TOKEN"))
            color = ConsoleColors.CYAN_BRIGHT;
        else if (kindString.contains("KEYWORD"))
            color = ConsoleColors.GREEN_BRIGHT;
        else
            color = ConsoleColors.PURPLE_BRIGHT;

        String marker = isLast ? "└──" : "├──";
        out.print(indent);
        out.print(marker);
        out.print(color + node.getKind());
        out.print(ConsoleColors.RESET);
        if (node instanceof SyntaxToken s && s.getValue() != null) {
            out.print(" ");
            out.print(s.getValue());
        }
        out.println();

        indent += isLast ? "    " : "│   ";

        SyntaxNode last = null;
        if (!node.getChildren().isEmpty()) {
            last = node.getChildren().get(node.getChildren().size() - 1);
        }
        for (SyntaxNode n : node.getChildren()) {
            printTree(out, n, indent, last == n);
        }
    }

}
