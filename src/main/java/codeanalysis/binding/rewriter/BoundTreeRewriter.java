package codeanalysis.binding.rewriter;

import codeanalysis.binding.BoundNode;
import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.binding.expression.assignment.BoundAssignmentExpression;
import codeanalysis.binding.expression.binary.BoundBinaryExpression;
import codeanalysis.binding.expression.literal.BoundLiteralExpression;
import codeanalysis.binding.expression.unary.BoundUnaryExpression;
import codeanalysis.binding.expression.variable.BoundVariableExpression;
import codeanalysis.binding.statement.BoundStatement;
import codeanalysis.binding.statement.block.BoundBlockStatement;
import codeanalysis.binding.statement.conditional.BoundElseClause;
import codeanalysis.binding.statement.conditional.BoundIfStatement;
import codeanalysis.binding.statement.declaration.BoundLabelDeclarationStatement;
import codeanalysis.binding.statement.declaration.BoundVariableDeclarationStatement;
import codeanalysis.binding.statement.expression.BoundExpressionStatement;
import codeanalysis.binding.statement.jumpto.BoundConditionalJumpToStatement;
import codeanalysis.binding.statement.jumpto.BoundJumpToStatement;
import codeanalysis.binding.statement.loop.BoundForConditionClause;
import codeanalysis.binding.statement.loop.BoundForStatement;
import codeanalysis.binding.statement.loop.BoundWhileStatement;

import java.util.ArrayList;
import java.util.List;

public abstract class BoundTreeRewriter {
    public BoundStatement rewriteStatement(BoundStatement statement) throws Exception {
        return switch (statement.getKind()) {
            case BLOCK_STATEMENT -> rewriteBlockStatement((BoundBlockStatement) statement);
            case EXPRESSION_STATEMENT -> rewriteExpressionStatement((BoundExpressionStatement) statement);
            case VARIABLE_DECLARATION_STATEMENT ->
                    rewriteVariableDeclarationStatement((BoundVariableDeclarationStatement) statement);
            case IF_STATEMENT -> rewriteIfStatement((BoundIfStatement) statement);
            case WHILE_STATEMENT -> rewriteWhileStatement((BoundWhileStatement) statement);
            case FOR_STATEMENT -> rewriteForStatement((BoundForStatement) statement);
            case LABEL_DECLARATION_STATEMENT ->
                    rewriteLabelDeclarationStatement((BoundLabelDeclarationStatement) statement);
            case JUMP_TO_STATEMENT -> rewriteJumpToStatement((BoundJumpToStatement) statement);
            case CONDITIONAL_JUMP_TO_STATEMENT ->
                    rewriteConditionalJumpToStatement((BoundConditionalJumpToStatement) statement);
            default -> throw new Exception("Unexpected statement " + statement.getKind());
        };
    }

    protected BoundStatement rewriteConditionalJumpToStatement(BoundConditionalJumpToStatement statement) throws Exception {
        BoundExpression condition = rewriteExpression(statement.getCondition());
        if (condition.equals(statement.getCondition()))
            return statement;
        return new BoundConditionalJumpToStatement(statement.getLabel(), condition, statement.isJumpIfFalse());
    }

    protected BoundStatement rewriteJumpToStatement(BoundJumpToStatement statement) {
        return statement;
    }

    protected BoundStatement rewriteLabelDeclarationStatement(BoundLabelDeclarationStatement statement) {
        return statement;
    }

    protected BoundStatement rewriteForStatement(BoundForStatement statement) throws Exception {
        BoundForConditionClause clause = rewriteForConditionClause(statement.getCondition());
        BoundStatement then = rewriteStatement(statement.getThenStatement());
        if (clause.equals(statement.getCondition()) && then.equals(statement.getThenStatement()))
            return statement;

        return new BoundForStatement(clause, then);
    }

    protected BoundStatement rewriteWhileStatement(BoundWhileStatement statement) throws Exception {
        BoundExpression condition = rewriteExpression(statement.getCondition());
        BoundStatement then = rewriteStatement(statement.getThenStatement());
        if (condition.equals(statement.getCondition()) && then.equals(statement.getThenStatement()))
            return statement;

        return new BoundWhileStatement(condition, then);
    }

    protected BoundStatement rewriteIfStatement(BoundIfStatement statement) throws Exception {
        BoundExpression expression = rewriteExpression(statement.getCondition());
        BoundStatement then = rewriteStatement(statement.getThenStatement());
        BoundStatement clause = statement.getElseClause() == null ? null :
                rewriteStatement(statement.getElseClause().getThenStatement());
        if (
                expression.equals((statement.getCondition()))
                        && then.equals(statement.getThenStatement())
                        && (clause == null || clause.equals(statement.getElseClause().getThenStatement()))
        )
            return statement;

        return new BoundIfStatement(expression, then, new BoundElseClause(clause));
    }

    protected BoundStatement rewriteVariableDeclarationStatement(BoundVariableDeclarationStatement statement) throws Exception {
        BoundExpression initializer = rewriteExpression(statement.getInitializer());
        if (initializer.equals(statement.getInitializer()))
            return statement;

        return new BoundVariableDeclarationStatement(statement.getVariable(), initializer);
    }

    protected BoundStatement rewriteExpressionStatement(BoundExpressionStatement statement) throws Exception {
        BoundExpression expression = rewriteExpression(statement.getExpression());
        if (expression.equals(statement.getExpression()))
            return statement;

        return new BoundExpressionStatement(expression);
    }

    protected BoundStatement rewriteBlockStatement(BoundBlockStatement statement) throws Exception {
        List<BoundStatement> statements = null;
        for (int i = 0; i < statement.getStatements().size(); i++) {
            BoundStatement oldStatement = statement.getStatements().get(i);
            BoundStatement newStatement = rewriteStatement(statement.getStatements().get(i));
            if (!newStatement.equals(oldStatement)) {
                if (statements == null) {
                    statements = new ArrayList<>();
                    for (int j = 0; j < i; j++)
                        statements.add(statement.getStatements().get(j));
                }
            }
            if (statements != null)
                statements.add(newStatement);
        }

        if (statements == null)
            return statement;
        return new BoundBlockStatement(List.copyOf(statements));
    }

    protected BoundForConditionClause rewriteForConditionClause(BoundForConditionClause clause) throws Exception {
        BoundNode variableClause;
        if (clause.getVariable().getKind().equals(BoundNodeKind.VARIABLE_DECLARATION_STATEMENT))
            variableClause = rewriteStatement((BoundStatement) clause.getVariable());
        else {
            variableClause = rewriteExpression((BoundExpression) clause.getVariable());
        }
        BoundExpression condition = rewriteExpression(clause.getConditionExpression());
        BoundExpression increment = rewriteExpression(clause.getIncrementExpression());
        if (
                variableClause.equals(clause.getVariable())
                        && condition.equals(clause.getConditionExpression())
                        && increment.equals(clause.getIncrementExpression())
        )
            return clause;
        return new BoundForConditionClause(variableClause, condition, increment);
    }

    public BoundExpression rewriteExpression(BoundExpression expression) throws Exception {
        return switch (expression.getKind()) {
            case LITERAL_EXPRESSION -> rewriteLiteralExpression((BoundLiteralExpression) expression);
            case VARIABLE_EXPRESSION -> rewriteVariableExpression((BoundVariableExpression) expression);
            case ASSIGNMENT_EXPRESSION -> rewriteAssignmentExpression((BoundAssignmentExpression) expression);
            case UNARY_EXPRESSION -> rewriteUnaryExpression((BoundUnaryExpression) expression);
            case BINARY_EXPRESSION -> rewriteBinaryExpression((BoundBinaryExpression) expression);
            default -> throw new Exception("Unexpected expression " + expression.getKind());
        };
    }

    protected BoundExpression rewriteBinaryExpression(BoundBinaryExpression node) throws Exception {
        BoundExpression left = rewriteExpression(node.getLeft());
        BoundExpression right = rewriteExpression(node.getRight());
        if (left.equals(node.getLeft()) && right.equals(node.getRight()))
            return node;
        return new BoundBinaryExpression(left, node.getOperator(), right);
    }

    protected BoundExpression rewriteAssignmentExpression(BoundAssignmentExpression node) throws Exception {
        BoundExpression expression = rewriteExpression(node.getBoundExpression());
        if (expression.equals(node.getBoundExpression()))
            return node;
        return new BoundAssignmentExpression(node.getVariable(), expression);
    }

    protected BoundExpression rewriteUnaryExpression(BoundUnaryExpression node) throws Exception {
        BoundExpression expression = rewriteExpression(node.getRight());
        if (expression.equals(node.getRight()))
            return node;
        return new BoundUnaryExpression(node.getOperator(), expression);
    }

    protected BoundExpression rewriteVariableExpression(BoundVariableExpression node) {
        return node;
    }

    protected BoundExpression rewriteLiteralExpression(BoundLiteralExpression node) {
        return node;
    }
}
