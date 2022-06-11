package codeanalysis.controlflow;

import codeanalysis.binding.statement.BoundStatement;
import codeanalysis.binding.statement.block.BoundBlockStatement;

import java.util.ArrayList;
import java.util.List;

public class BasicBlockBuilder {
    private final List<BasicBlock> blocks = new ArrayList<>();
    private final List<BoundStatement> statements = new ArrayList<>();

    public List<BasicBlock> build(BoundBlockStatement body) {
        for (var statement : body.getStatements()) {
            switch (statement.getKind()) {
                case VARIABLE_DECLARATION_STATEMENT, EXPRESSION_STATEMENT -> {
                    statements.add(statement);
                }
                case JUMP_TO_STATEMENT, CONDITIONAL_JUMP_TO_STATEMENT, RETURN_STATEMENT -> {
                    statements.add(statement);
                    startBlock();
                }
                case LABEL_DECLARATION_STATEMENT -> {
                    startBlock();
                    statements.add(statement);
                }
                default -> throw new RuntimeException("Unexpected node " + statement.getKind());
            }
        }
        endBlock();
        return blocks;
    }

    private void startBlock() {
        endBlock();
    }

    private void endBlock() {
        if (!statements.isEmpty()) {
            var block = new BasicBlock();
            block.getStatements().addAll(statements);
            blocks.add(block);
            statements.clear();
        }
    }
}
