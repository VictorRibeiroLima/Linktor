package codeanalysis.syntax;

import codeanalysis.diagnostics.TextSpan;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public abstract class SyntaxNode {
    public abstract SyntaxKind getKind();

    public abstract List<SyntaxNode> getChildren();

    public TextSpan getSpan() {
        TextSpan first = null;
        TextSpan last = null;
        if (!this.getChildren().isEmpty()) {
            first = this.getChildren().get(0).getSpan();
            last = this.getChildren().get(this.getChildren().size() - 1).getSpan();
        }
        return TextSpan.fromBounds(first.start(), last.end());
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
        String marker = isLast ? "└──" : "├──";
        out.print(indent);
        out.print(marker);
        out.print(node.getKind());
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
