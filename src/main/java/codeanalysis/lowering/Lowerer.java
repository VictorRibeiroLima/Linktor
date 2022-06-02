package codeanalysis.lowering;

import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.binding.rewriter.BoundTreeRewriter;
import codeanalysis.binding.statement.BoundStatement;
import codeanalysis.binding.statement.block.BoundBlockStatement;
import codeanalysis.binding.statement.declaration.BoundVariableDeclarationStatement;
import codeanalysis.binding.statement.expression.BoundExpressionStatement;
import codeanalysis.binding.statement.loop.BoundForConditionClause;
import codeanalysis.binding.statement.loop.BoundForStatement;
import codeanalysis.binding.statement.loop.BoundWhileStatement;

import java.util.ArrayList;
import java.util.List;

public final class Lowerer extends BoundTreeRewriter {

    private Lowerer() {
    }

    public static BoundStatement lower(BoundStatement statement) throws Exception {
        Lowerer lowerer = new Lowerer();
        return lowerer.rewriteStatement(statement);
    }

    @Override
    protected BoundStatement rewriteForStatement(BoundForStatement statement) throws Exception {
        List<BoundStatement> statements = new ArrayList<>();
        BoundForConditionClause clause = statement.getCondition();
        if (statement.getCondition().getVariable().getKind() == BoundNodeKind.VARIABLE_DECLARATION_STATEMENT) {
            BoundVariableDeclarationStatement variableDeclaration =
                    (BoundVariableDeclarationStatement) clause.getVariable();
            statements.add(variableDeclaration);
        }
        BoundExpression condition = clause.getConditionExpression();
        BoundStatement increment = new BoundExpressionStatement(clause.getIncrementExpression());
        BoundBlockStatement whileBlock = new BoundBlockStatement(
                List.of(statement.getThenStatement(), increment)
        );
        BoundWhileStatement whileStatement = new BoundWhileStatement(condition, whileBlock);
        statements.add(whileStatement);

        BoundBlockStatement block = new BoundBlockStatement(List.copyOf(statements));
        return rewriteStatement(block);
    }
}
