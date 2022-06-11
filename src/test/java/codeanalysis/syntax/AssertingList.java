package codeanalysis.syntax;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class AssertingList {
    private final Enumeration<SyntaxNode> nodes;
    private SyntaxNode current;

    public AssertingList(SyntaxNode node) {
        this.nodes = AssertingList.flatten(node);
    }

    public void assertToken(SyntaxKind kind, String text) {
        assertTrue(nodes.hasMoreElements());
        current = nodes.nextElement();
        assertEquals(SyntaxToken.class, current.getClass());
        assertEquals(kind, current.getKind());
        assertEquals(text, ((SyntaxToken) current).getText());
    }

    public void assertNode(SyntaxKind kind) {
        assertTrue(nodes.hasMoreElements());
        current = nodes.nextElement();
        assertNotEquals(SyntaxNode.class, current.getClass());
        assertEquals(kind, current.getKind());
    }


    private static Enumeration<SyntaxNode> flatten(SyntaxNode node) {
        final Stack<SyntaxNode> stack = new Stack<>();
        final List<SyntaxNode> nodes = new ArrayList<>();
        stack.add(node);

        while (stack.size() > 0) {
            SyntaxNode n = stack.pop();
            nodes.add(n);
            List<SyntaxNode> children = n.getChildren();
            var copyArray = new ArrayList<>(children);
            Collections.reverse(copyArray);
            for (SyntaxNode child : copyArray) {
                stack.push(child);
            }
        }
        return Collections.enumeration(nodes);
    }
}