package codeanalysis.syntax.expression;

import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.SyntaxToken;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SeparatedSyntaxList<T extends SyntaxNode> implements Iterable<T> {
    private final List<SyntaxNode> separatorAndNodes;
    private final int count;

    public SeparatedSyntaxList(List<SyntaxNode> separatorAndNodes) {
        this.separatorAndNodes = List.copyOf(separatorAndNodes);
        this.count = (separatorAndNodes.size() + 1) / 2;

    }

    @Override
    public Iterator<T> iterator() {
        List<T> nodes = new ArrayList<>();
        for (int i = 0; i < this.count; i++) {
            nodes.add(this.get(i));
        }
        return nodes.iterator();
    }

    public T get(int index) {
        return (T) this.separatorAndNodes.get(index * 2);
    }

    public SyntaxToken getSeparator(int index) {
        if (index == count - 1)
            return null;
        return (SyntaxToken) this.separatorAndNodes.get(index * 2 + 1);
    }

    public List<SyntaxNode> getSeparatorAndNodes() {
        return separatorAndNodes;
    }

    public int getCount() {
        return count;
    }
}
