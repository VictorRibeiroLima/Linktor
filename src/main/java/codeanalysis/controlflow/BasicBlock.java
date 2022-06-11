package codeanalysis.controlflow;

import codeanalysis.binding.statement.BoundStatement;
import io.BoundNodeWriter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class BasicBlock {
    private final List<BoundStatement> statements = new ArrayList<>();
    private final List<BasicBlockEdge> incoming = new ArrayList<>();
    private final List<BasicBlockEdge> outgoing = new ArrayList<>();
    private boolean isStart;
    private boolean isEnd;

    public BasicBlock() {
    }

    public BasicBlock(boolean isStart) {
        this.isStart = isStart;
        this.isEnd = !isStart;
    }

    public List<BoundStatement> getStatements() {
        return statements;
    }

    public List<BasicBlockEdge> getIncoming() {
        return incoming;
    }

    public List<BasicBlockEdge> getOutgoing() {
        return outgoing;
    }

    public boolean isStart() {
        return isStart;
    }

    public boolean isEnd() {
        return isEnd;
    }

    @Override
    public String toString() {
        if (isStart) {
            return "<START>";
        } else if (isEnd) {
            return "<END>";
        }

        var stringWriter = new StringWriter();
        var printWriter = new PrintWriter(stringWriter);
        for (var statement : statements) {
            BoundNodeWriter.writeTo(printWriter, statement);
        }
        return stringWriter.toString();
    }
}
