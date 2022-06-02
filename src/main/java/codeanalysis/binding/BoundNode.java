package codeanalysis.binding;

import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.binding.expression.binary.BoundBinaryExpression;
import codeanalysis.binding.expression.unary.BoundUnaryExpression;
import codeanalysis.binding.statement.BoundStatement;
import util.ConsoleColors;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class BoundNode {
    public abstract BoundNodeKind getKind();

    public abstract List<BoundNode> getChildren();

    public void printTree(PrintWriter out) throws Exception {
        printTree(out, this, "", false);
    }

    private void printTree(PrintWriter out, BoundNode node, String indent, boolean isLast) throws Exception {


        String marker = isLast ? "└──" : "├──";
        out.print(indent);
        out.print(marker);
        writeNode(out, node);
        writeProperties(out, node);
        out.println();

        indent += isLast ? "    " : "│   ";

        BoundNode last = null;
        if (!node.getChildren().isEmpty()) {
            last = node.getChildren().get(node.getChildren().size() - 1);
        }
        for (BoundNode n : node.getChildren()) {
            printTree(out, n, indent, last == n);
        }
    }

    private void writeProperties(PrintWriter out, BoundNode node) throws Exception {
        for (Property p : node.getProperties()) {
            out.print(" " + p.name);
            out.print(" = ");
            out.print(p.value);
        }
    }

    private static void writeNode(PrintWriter out, BoundNode node) {
        String color = getColor(node);
        out.print(color);
        String text = getText(node);
        out.print(text);
        out.print(ConsoleColors.RESET);
    }

    private static String getText(BoundNode node) {
        if (node instanceof BoundBinaryExpression b)
            return b.getOperator().getKind().toString() + "_EXPRESSION";
        else if (node instanceof BoundUnaryExpression b)
            return b.getOperator().getKind().toString() + "_EXPRESSION";

        return node.getKind().toString();
    }

    private static String getColor(BoundNode node) {
        if (node instanceof BoundExpression)
            return ConsoleColors.BLUE_BRIGHT;
        else if (node instanceof BoundStatement)
            return ConsoleColors.CYAN_BRIGHT;

        return ConsoleColors.PURPLE_BRIGHT;
    }

    private List<Property> getProperties() throws Exception {
        List<Property> properties = new ArrayList<>();
        List<PropertyDescriptor> descriptors = List.of(Introspector.getBeanInfo(this.getClass(), Object.class)
                .getPropertyDescriptors());
        for (PropertyDescriptor pd : descriptors) {
            if (Objects.isNull(pd.getReadMethod()))
                continue;
            try {
                Object value = pd.getReadMethod().invoke(this);
                if ((value instanceof BoundNode)
                        || (value instanceof BoundNodeKind)
                        || (value instanceof List<?>)
                        || pd.getDisplayName().contains("operator")
                )
                    continue;
                properties.add(new Property(pd.getDisplayName(), value));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return properties;
    }

    private record Property(String name, Object value) {
    }
}
