package codeanalysis.controlflow;

import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.binding.expression.literal.BoundLiteralExpression;
import codeanalysis.binding.expression.unary.BoundUnaryExpression;
import codeanalysis.binding.expression.unary.BoundUnaryOperator;
import codeanalysis.binding.statement.BoundStatement;
import codeanalysis.binding.statement.declaration.BoundLabelDeclarationStatement;
import codeanalysis.binding.statement.jumpto.BoundConditionalJumpToStatement;
import codeanalysis.binding.statement.jumpto.BoundJumpToStatement;
import codeanalysis.binding.statement.jumpto.BoundLabel;
import codeanalysis.symbol.TypeSymbol;
import codeanalysis.syntax.SyntaxKind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GraphBuilder {
    private final HashMap<BoundStatement, BasicBlock> blockFromStatement = new HashMap<>();
    private final HashMap<BoundLabel, BasicBlock> blockFromLabel = new HashMap<>();
    private final BasicBlock start = new BasicBlock(true);
    private final BasicBlock end = new BasicBlock(false);
    private final List<BasicBlock> blocks = new ArrayList<>();
    private final List<BasicBlockEdge> edges = new ArrayList<>();

    public ControlFlowGraph build(List<BasicBlock> listBlocks) {
        this.blocks.addAll(listBlocks);
        var start = new BasicBlock(true);
        var end = new BasicBlock(false);

        if (blocks.isEmpty())
            connect(start, end);
        else
            connect(start, blocks.get(0));

        setBlockFromStatement();

        for (int i = 0; i < blocks.size(); i++) {
            var current = blocks.get(i);
            var next = i == blocks.size() - 1 ? end : blocks.get(i + 1);
            for (var statement : current.getStatements()) {
                var isLast = statement == current.getStatements().get(current.getStatements().size() - 1);
                switch (statement.getKind()) {
                    case JUMP_TO_STATEMENT -> {
                        var js = (BoundJumpToStatement) statement;
                        var toBlock = blockFromLabel.get(js.getLabel());
                        connect(current, toBlock);
                    }
                    case CONDITIONAL_JUMP_TO_STATEMENT -> {
                        var cjs = (BoundConditionalJumpToStatement) statement;
                        var thenBlock = blockFromLabel.get(cjs.getLabel());
                        var elsBlock = next;
                        var negatedCondition = negate(cjs.getCondition());
                        var thenCondition = cjs.isJumpIfTrue() ? cjs.getCondition() : negatedCondition;
                        var elseCondition = cjs.isJumpIfTrue() ? negatedCondition : cjs.getCondition();
                        connect(current, thenBlock, thenCondition);
                        connect(current, elsBlock, elseCondition);
                    }
                    case RETURN_STATEMENT -> {
                        connect(current, end);
                    }
                    case LABEL_DECLARATION_STATEMENT, VARIABLE_DECLARATION_STATEMENT, EXPRESSION_STATEMENT -> {
                        if (isLast)
                            connect(current, next);
                    }
                    default -> throw new RuntimeException("Unexpected node " + statement.getKind());
                }
            }
        }

        var scanAgain = false;
        do {
            scanAgain = false;
            for (var block : blocks) {
                if (block.getIncoming().isEmpty()) {
                    removeBlock(block);
                    scanAgain = true;
                    break;
                }
            }

        } while (scanAgain);

        blocks.add(0, start);
        blocks.add(end);
        return new ControlFlowGraph(start, end, blocks, edges);
    }

    private void removeBlock(BasicBlock block) {
        for (var edge : block.getIncoming()) {
            edge.getFrom().getOutgoing().remove(edge);
            edges.remove(edge);
        }
        for (var edge : block.getOutgoing()) {
            edge.getTo().getIncoming().remove(edge);
            edges.remove(edge);
        }

        blocks.remove(block);
    }

    private BoundExpression negate(BoundExpression condition) {
        if (condition instanceof BoundLiteralExpression l) {
            var value = !(boolean) l.getValue();
            return new BoundLiteralExpression(value);
        }
        var op = BoundUnaryOperator.bind(SyntaxKind.EXCLAMATION_TOKEN, TypeSymbol.BOOLEAN);
        return new BoundUnaryExpression(op, condition);
    }

    private void setBlockFromStatement() {
        for (var block : blocks) {
            for (var statement : block.getStatements()) {
                blockFromStatement.put(statement, block);
                if (statement instanceof BoundLabelDeclarationStatement label)
                    blockFromLabel.put(label.getLabel(), block);
            }
        }
    }

    private void connect(BasicBlock from, BasicBlock to) {
        connect(from, to, null);
    }

    private void connect(BasicBlock from, BasicBlock to, BoundExpression condition) {
        if (condition instanceof BoundLiteralExpression l) {
            var value = (boolean) l.getValue();
            if (value)
                condition = null;
            else
                return;
        }
        var edge = new BasicBlockEdge(from, to, condition);
        from.getOutgoing().add(edge);
        to.getIncoming().add(edge);
        edges.add(edge);
    }
}
