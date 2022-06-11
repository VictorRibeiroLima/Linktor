package codeanalysis.controlflow;

import codeanalysis.binding.expression.BoundExpression;
import io.BoundNodeWriter;

import java.io.PrintWriter;
import java.io.StringWriter;

public class BasicBlockEdge {
    private final BasicBlock from;
    private final BasicBlock to;
    private final BoundExpression condition;

    public BasicBlockEdge(BasicBlock from, BasicBlock to, BoundExpression condition) {
        this.from = from;
        this.to = to;
        this.condition = condition;
    }

    public BasicBlock getFrom() {
        return from;
    }

    public BasicBlock getTo() {
        return to;
    }

    public BoundExpression getCondition() {
        return condition;
    }

    @Override
    public String toString() {
        if (condition == null)
            return "";
        var stringWriter = new StringWriter();
        var printWriter = new PrintWriter(stringWriter);
        BoundNodeWriter.writeTo(printWriter, condition);
        return stringWriter.toString();
    }
}
