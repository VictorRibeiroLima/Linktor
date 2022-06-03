package codeanalysis.lowering;

import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.binding.rewriter.BoundTreeRewriter;
import codeanalysis.binding.statement.BoundStatement;
import codeanalysis.binding.statement.block.BoundBlockStatement;
import codeanalysis.binding.statement.conditional.BoundIfStatement;
import codeanalysis.binding.statement.declaration.BoundLabelDeclarationStatement;
import codeanalysis.binding.statement.declaration.BoundVariableDeclarationStatement;
import codeanalysis.binding.statement.expression.BoundExpressionStatement;
import codeanalysis.binding.statement.jumpto.BoundConditionalJumpToStatement;
import codeanalysis.binding.statement.jumpto.BoundJumpToStatement;
import codeanalysis.binding.statement.loop.BoundForConditionClause;
import codeanalysis.binding.statement.loop.BoundForStatement;
import codeanalysis.binding.statement.loop.BoundWhileStatement;
import codeanalysis.symbol.LabelSymbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public final class Lowerer extends BoundTreeRewriter {

    private int labelCont = 0;

    private Lowerer() {
    }

    public static BoundBlockStatement lower(BoundStatement statement) throws Exception {
        Lowerer lowerer = new Lowerer();
        BoundStatement result = lowerer.rewriteStatement(statement);
        return flatten(result);
    }

    private static BoundBlockStatement flatten(BoundStatement statement) {
        List<BoundStatement> statements = new ArrayList<>();
        Stack<BoundStatement> stack = new Stack<>();
        stack.push(statement);
        while (!stack.empty()) {
            BoundStatement current = stack.pop();
            if (current instanceof BoundBlockStatement b) {
                List<BoundStatement> statementsCopy = new ArrayList<>(b.getStatements());
                Collections.reverse(statementsCopy);
                for (BoundStatement s : statementsCopy)
                    stack.add(s);
            } else {
                statements.add(current);
            }
        }
        return new BoundBlockStatement(List.copyOf(statements));
    }

    @Override
    protected BoundStatement rewriteIfStatement(BoundIfStatement statement) throws Exception {
        BoundBlockStatement result = null;
        if (statement.getElseClause() == null) {
            /*
            {
                if<condition>
                    <then>
            }
            ----->
            {
                jumpToFalse <condition> end
                <then>
                end:
            }
         */
            LabelSymbol endLabel = genLabel();
            BoundConditionalJumpToStatement jumpToFalse =
                    new BoundConditionalJumpToStatement(endLabel, statement.getCondition(), true);
            BoundLabelDeclarationStatement end = new BoundLabelDeclarationStatement(endLabel);
            result = new BoundBlockStatement(List.of(jumpToFalse, statement.getThenStatement(), end));
        } else {

        /*
            {
                if<condition>
                    <then>
                else
                    <elseThen>
            }
            ----->
            {
                jumpToFalse <condition> else
                <then>
                jumpTo end
                else:
                <elseThen>
                end:
            }
         */
            LabelSymbol elseLabel = genLabel();
            LabelSymbol endLabel = genLabel();
            BoundConditionalJumpToStatement jumpToFalse =
                    new BoundConditionalJumpToStatement(elseLabel, statement.getCondition(), true);
            BoundJumpToStatement jumpToEnd = new BoundJumpToStatement(endLabel);
            BoundLabelDeclarationStatement elseS = new BoundLabelDeclarationStatement(elseLabel);
            BoundLabelDeclarationStatement end = new BoundLabelDeclarationStatement(endLabel);
            result = new BoundBlockStatement(
                    List.of(
                            jumpToFalse,
                            statement.getThenStatement(),
                            jumpToEnd,
                            elseS,
                            statement.getElseClause().getThenStatement(),
                            end
                    )
            );
        }
        return rewriteStatement(result);
    }

    @Override
    protected BoundStatement rewriteWhileStatement(BoundWhileStatement statement) throws Exception {
        /*
            {
                while <check>{
                    <loop>
                }
             }
             ----->

             jumpTo check
             continue:
             <loop>
             check:
             jumpToTrue <condition> continue:
             end:
         */
        LabelSymbol checkLabel = genLabel();
        LabelSymbol continueLabel = genLabel();
        LabelSymbol endLabel = genLabel();
        BoundJumpToStatement jumpToCheck = new BoundJumpToStatement(checkLabel);
        BoundConditionalJumpToStatement jumpToTrue =
                new BoundConditionalJumpToStatement(continueLabel, statement.getCondition());
        BoundLabelDeclarationStatement check = new BoundLabelDeclarationStatement(checkLabel);
        BoundLabelDeclarationStatement continueS = new BoundLabelDeclarationStatement(continueLabel);
        BoundLabelDeclarationStatement end = new BoundLabelDeclarationStatement(endLabel);

        BoundBlockStatement result = new BoundBlockStatement(
                List.of(jumpToCheck, continueS, statement.getThenStatement(), check, jumpToTrue, end)
        );
        return rewriteStatement(result);
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

    private LabelSymbol genLabel() {
        String label = "Label" + (++labelCont);
        return new LabelSymbol(label);
    }
}
