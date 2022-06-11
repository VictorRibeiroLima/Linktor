package codeanalysis.controlflow;

import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.statement.block.BoundBlockStatement;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

public class ControlFlowGraph {

    private final BasicBlock start;
    private final BasicBlock end;
    private final List<BasicBlock> blocks;
    private final List<BasicBlockEdge> edges;

    protected ControlFlowGraph(BasicBlock start, BasicBlock end, List<BasicBlock> blocks, List<BasicBlockEdge> edges) {
        this.start = start;
        this.end = end;
        this.blocks = blocks;
        this.edges = edges;
    }

    public static ControlFlowGraph create(BoundBlockStatement body) {
        var basicBlockBuilder = new BasicBlockBuilder();
        var blocks = basicBlockBuilder.build(body);
        var graphBuilder = new GraphBuilder();
        return graphBuilder.build(blocks);
    }

    public static boolean allPathsReturn(BoundBlockStatement body) {
        var graph = create(body);
        for (var branch : graph.getEnd().getIncoming()) {
            var last = branch.getFrom().getStatements().get(branch.getFrom().getStatements().size() - 1);
            if (last.getKind() != BoundNodeKind.RETURN_STATEMENT)
                return false;
        }
        return true;
    }

    public BasicBlock getStart() {
        return start;
    }

    public BasicBlock getEnd() {
        return end;
    }

    public List<BasicBlock> getBlocks() {
        return blocks;
    }

    public List<BasicBlockEdge> getEdges() {
        return edges;
    }


    public void writeTo(PrintWriter out) {
        out.println("digraph G {");
        var blockIds = new HashMap<BasicBlock, String>();
        for (int i = 0; i < blocks.size(); i++) {
            var id = "N" + i;
            BasicBlock block = blocks.get(i);
            blockIds.put(block, id);
        }
        for (var block : blocks) {
            var id = blockIds.get(block);
            var label = block.toString()
                    .replaceAll("\u001B\\[[;\\d]*m", "")
                    .replaceAll("\"", "'");

            out.println("    " + id + " [label = \"" + label + "\" shape=box]");
        }
        for (var edge : edges) {
            var fromId = blockIds.get(edge.getFrom());
            var toId = blockIds.get(edge.getTo());
            var label = edge.toString()
                    .replaceAll("\u001B\\[[;\\d]*m", "")
                    .replaceAll("\"", "'");
            out.println("    " + fromId + " -> " + toId + " [label = \"" + label + "\"]");
        }
        out.println("}");
        out.flush();
        out.close();
    }
}
