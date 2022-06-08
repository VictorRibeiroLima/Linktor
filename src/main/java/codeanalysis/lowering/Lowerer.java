package codeanalysis.lowering;

import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.binding.expression.assignment.BoundAssignmentExpression;
import codeanalysis.binding.expression.assignment.BoundOperationAssignmentExpression;
import codeanalysis.binding.expression.assignment.BoundOperationAssignmentOperatorKind;
import codeanalysis.binding.expression.binary.BoundBinaryExpression;
import codeanalysis.binding.expression.binary.BoundBinaryOperator;
import codeanalysis.binding.expression.literal.BoundLiteralExpression;
import codeanalysis.binding.expression.sufixpreffix.BoundPrefixExpression;
import codeanalysis.binding.expression.sufixpreffix.BoundPrefixSuffixOperatorKind;
import codeanalysis.binding.expression.sufixpreffix.BoundSuffixExpression;
import codeanalysis.binding.expression.variable.BoundVariableExpression;
import codeanalysis.binding.rewriter.BoundTreeRewriter;
import codeanalysis.binding.statement.BoundStatement;
import codeanalysis.binding.statement.block.BoundBlockStatement;
import codeanalysis.binding.statement.conditional.BoundIfStatement;
import codeanalysis.binding.statement.declaration.BoundLabelDeclarationStatement;
import codeanalysis.binding.statement.declaration.BoundVariableDeclarationStatement;
import codeanalysis.binding.statement.expression.BoundExpressionStatement;
import codeanalysis.binding.statement.jumpto.BoundConditionalJumpToStatement;
import codeanalysis.binding.statement.jumpto.BoundJumpToStatement;
import codeanalysis.binding.statement.jumpto.BoundLabel;
import codeanalysis.binding.statement.loop.BoundForConditionClause;
import codeanalysis.binding.statement.loop.BoundForStatement;
import codeanalysis.binding.statement.loop.BoundWhileStatement;
import codeanalysis.syntax.SyntaxKind;

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
                stack.addAll(statementsCopy);
            } else {
                statements.add(current);
            }
        }
        return new BoundBlockStatement(List.copyOf(statements));
    }

    protected BoundExpression rewriteOperationAssignmentExpression(BoundOperationAssignmentExpression node) throws Exception {
        /*
            a += 2;
            a=a+2;
         */
        var left = new BoundVariableExpression(node.getVariable());
        BoundBinaryOperator operator;
        var right = node.getBoundExpression();
        if (node.getOperator().getKind() == BoundOperationAssignmentOperatorKind.INCREMENT || node.getOperator().getKind() == BoundOperationAssignmentOperatorKind.CONCATENATION)
            operator = BoundBinaryOperator.bind(SyntaxKind.PLUS_TOKEN, left.getType(), right.getType());
        else
            operator = BoundBinaryOperator.bind(SyntaxKind.MINUS_TOKEN, left.getType(), right.getType());
        var binary = new BoundBinaryExpression(left, operator, right);

        var assignment = new BoundAssignmentExpression(node.getVariable(), binary);
        return rewriteExpression(assignment);
    }

    protected BoundExpression rewriteSuffixExpression(BoundSuffixExpression expression) throws Exception {
        /*
            i++
            --------
            (i=i+1)-1
         */
        var left = new BoundVariableExpression(expression.getLeft());
        BoundBinaryOperator operator;
        BoundBinaryOperator returnOperator;
        var right = new BoundLiteralExpression(1);
        if (expression.getOperator().getKind() == BoundPrefixSuffixOperatorKind.INCREMENT) {
            operator = BoundBinaryOperator.bind(SyntaxKind.PLUS_TOKEN, left.getType(), right.getType());
            returnOperator = BoundBinaryOperator.bind(SyntaxKind.MINUS_TOKEN, left.getType(), right.getType());
        } else {
            operator = BoundBinaryOperator.bind(SyntaxKind.MINUS_TOKEN, left.getType(), right.getType());
            returnOperator = BoundBinaryOperator.bind(SyntaxKind.PLUS_TOKEN, left.getType(), right.getType());
        }
        var binary = new BoundBinaryExpression(left, operator, right);

        var assignment = new BoundAssignmentExpression(expression.getLeft(), binary);
        var result = new BoundBinaryExpression(assignment, returnOperator, right);
        return rewriteExpression(result);
    }

    @Override
    protected BoundExpression rewritePrefixExpression(BoundPrefixExpression expression) throws Exception {
        /*
            i++
            --------
            i=i+1
         */
        var left = new BoundVariableExpression(expression.getRight());
        BoundBinaryOperator operator;
        var right = new BoundLiteralExpression(1);
        if (expression.getOperator().getKind() == BoundPrefixSuffixOperatorKind.INCREMENT)
            operator = BoundBinaryOperator.bind(SyntaxKind.PLUS_TOKEN, left.getType(), right.getType());
        else
            operator = BoundBinaryOperator.bind(SyntaxKind.MINUS_TOKEN, left.getType(), right.getType());
        var binary = new BoundBinaryExpression(left, operator, right);

        var assignment = new BoundAssignmentExpression(expression.getRight(), binary);
        return rewriteExpression(assignment);
    }

    @Override
    protected BoundStatement rewriteIfStatement(BoundIfStatement statement) throws Exception {
        BoundBlockStatement result;
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
            BoundLabel endLabel = genLabel();
            BoundConditionalJumpToStatement jumpToFalse =
                    new BoundConditionalJumpToStatement(endLabel, statement.getCondition(), false);
            BoundLabelDeclarationStatement end = new BoundLabelDeclarationStatement(endLabel);
            result = new BoundBlockStatement(List.of(jumpToFalse, statement.getThenStatement(), end));
        } else {

        /*
            {
                if<condition>
                    <then>s
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
            BoundLabel elseLabel = genLabel();
            BoundLabel endLabel = genLabel();
            BoundConditionalJumpToStatement jumpToFalse =
                    new BoundConditionalJumpToStatement(elseLabel, statement.getCondition(), false);
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
             continue:
             jumpTo check
             body:
             <loop>
             check:
             jumpToTrue <condition> continue:
             end:
         */
        BoundLabel continueLabel = statement.getContinueLabel();
        BoundLabel checkLabel = genLabel();
        BoundLabel bodyLabel = genLabel();
        BoundLabel endLabel = statement.getBreakLabel();
        BoundJumpToStatement jumpToCheck = new BoundJumpToStatement(checkLabel);
        BoundConditionalJumpToStatement jumpToTrue =
                new BoundConditionalJumpToStatement(bodyLabel, statement.getCondition());
        BoundLabelDeclarationStatement continueS = new BoundLabelDeclarationStatement(continueLabel);
        BoundLabelDeclarationStatement check = new BoundLabelDeclarationStatement(checkLabel);
        BoundLabelDeclarationStatement bodyS = new BoundLabelDeclarationStatement(bodyLabel);
        BoundLabelDeclarationStatement end = new BoundLabelDeclarationStatement(endLabel);

        BoundBlockStatement result = new BoundBlockStatement(
                List.of(continueS, jumpToCheck, bodyS, statement.getThenStatement(), check, jumpToTrue, end)
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
        BoundLabelDeclarationStatement continueLabel = new BoundLabelDeclarationStatement(statement.getContinueLabel());
        BoundStatement increment = new BoundExpressionStatement(clause.getIncrementExpression());
        BoundBlockStatement whileBlock = new BoundBlockStatement(
                List.of(
                        statement.getThenStatement(),
                        continueLabel,
                        increment)
        );
        BoundWhileStatement whileStatement = new BoundWhileStatement(condition, whileBlock, statement.getBreakLabel(), genLabel());
        statements.add(whileStatement);

        BoundBlockStatement block = new BoundBlockStatement(List.copyOf(statements));
        return rewriteStatement(block);
    }

    private BoundLabel genLabel() {
        String label = "Label" + (++labelCont);
        return new BoundLabel(label);
    }
}
