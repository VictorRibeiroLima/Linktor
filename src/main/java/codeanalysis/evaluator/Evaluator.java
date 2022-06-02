package codeanalysis.evaluator;

import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.binding.expression.assignment.BoundAssignmentExpression;
import codeanalysis.binding.expression.binary.BoundBinaryExpression;
import codeanalysis.binding.expression.literal.BoundLiteralExpression;
import codeanalysis.binding.expression.unary.BoundUnaryExpression;
import codeanalysis.binding.expression.variable.BoundVariableExpression;
import codeanalysis.binding.statement.BoundStatement;
import codeanalysis.binding.statement.block.BoundBlockStatement;
import codeanalysis.binding.statement.conditional.BoundIfStatement;
import codeanalysis.binding.statement.declaration.BoundVariableDeclarationStatement;
import codeanalysis.binding.statement.expression.BoundExpressionStatement;
import codeanalysis.binding.statement.loop.BoundForConditionClause;
import codeanalysis.binding.statement.loop.BoundForStatement;
import codeanalysis.binding.statement.loop.BoundWhileStatement;
import codeanalysis.symbol.VariableSymbol;

import java.util.Map;

public final class Evaluator {
    private final BoundStatement root;
    private final Map<VariableSymbol, Object> variables;

    private Object lastValue;

    public Evaluator(BoundStatement root, Map<VariableSymbol, Object> variables) {
        this.root = root;
        this.variables = variables;
    }

    public Object evaluate() throws Exception {
        evaluateStatement(root);
        return lastValue;
    }

    private void evaluateStatement(BoundStatement statement) throws Exception {
        switch (statement.getKind()) {
            case BLOCK_STATEMENT -> evaluateBlockStatement((BoundBlockStatement) statement);
            case EXPRESSION_STATEMENT -> evaluateExpressionStatement((BoundExpressionStatement) statement);
            case VARIABLE_DECLARATION_STATEMENT ->
                    evaluateVariableDeclarationStatement((BoundVariableDeclarationStatement) statement);
            case IF_STATEMENT -> evaluateIfStatement((BoundIfStatement) statement);
            case WHILE_STATEMENT -> evaluateWhileStatement((BoundWhileStatement) statement);
            case FOR_STATEMENT -> evaluateForStatement((BoundForStatement) statement);
            default -> throw new Exception("Unexpected node " + statement.getKind());


        }
    }

    private void evaluateForStatement(BoundForStatement statement) throws Exception {
        BoundForConditionClause clause = statement.getCondition();
        if (clause.getVariable().getKind() == BoundNodeKind.VARIABLE_DECLARATION_STATEMENT)
            evaluateVariableDeclarationStatement((BoundVariableDeclarationStatement) clause.getVariable());
        while ((boolean) evaluateExpression(clause.getConditionExpression())) {
            evaluateStatement(statement.getThenStatement());
            evaluateExpression(clause.getIncrementExpression());
        }
    }

    private void evaluateWhileStatement(BoundWhileStatement statement) throws Exception {
        while ((boolean) evaluateExpression(statement.getCondition())) {
            evaluateStatement(statement.getThenStatement());
        }
    }

    private void evaluateIfStatement(BoundIfStatement statement) throws Exception {
        boolean condition = (boolean) evaluateExpression(statement.getCondition());
        if (condition) {
            evaluateStatement(statement.getThenStatement());
        } else {
            if (statement.getElseClause() != null) {
                evaluateStatement(statement.getElseClause().getThenStatement());
            }
        }
    }

    private void evaluateVariableDeclarationStatement(BoundVariableDeclarationStatement statement) throws Exception {
        Object value = evaluateExpression(statement.getInitializer());
        variables.put(statement.getVariable(), value);
        lastValue = value;
    }

    private void evaluateBlockStatement(BoundBlockStatement statement) throws Exception {
        for (BoundStatement b : statement.getStatements()) {
            evaluateStatement(b);
        }
    }

    private void evaluateExpressionStatement(BoundExpressionStatement statement) throws Exception {
        lastValue = evaluateExpression(statement.getExpression());
    }

    private Object evaluateExpression(BoundExpression node) throws Exception {
        return switch (node.getKind()) {
            case LITERAL_EXPRESSION -> evaluateLiteralExpression((BoundLiteralExpression) node);
            case VARIABLE_EXPRESSION -> evaluateVariableExpression((BoundVariableExpression) node);
            case ASSIGNMENT_EXPRESSION -> evaluateAssignmentExpression((BoundAssignmentExpression) node);
            case UNARY_EXPRESSION -> evaluateUnaryExpression((BoundUnaryExpression) node);
            case BINARY_EXPRESSION -> evaluateBinaryExpression((BoundBinaryExpression) node);
            default -> throw new Exception("Unexpected node " + node.getKind());
        };
    }

    private Object evaluateLiteralExpression(BoundLiteralExpression l) {
        return l.getValue();
    }

    private Object evaluateVariableExpression(BoundVariableExpression v) {
        return variables.get(v.getVariable());
    }

    private Object evaluateAssignmentExpression(BoundAssignmentExpression a) throws Exception {
        Object value = evaluateExpression(a.getBoundExpression());
        variables.put(a.getVariable(), value);
        return value;
    }

    private Object evaluateUnaryExpression(BoundUnaryExpression u) throws Exception {
        Object value = evaluateExpression(u.getRight());
        return switch (u.getOperator().getKind()) {
            case IDENTITY -> value;
            case NEGATION -> -(int) value;
            case LOGICAL_NEGATION -> !(boolean) value;
            default -> throw new Exception("Unexpected unary operation " + u.getOperator());
        };
    }

    private Object evaluateBinaryExpression(BoundBinaryExpression b) throws Exception {
        Object left = evaluateExpression(b.getLeft());
        Object right = evaluateExpression(b.getRight());
        return switch (b.getOperator().getKind()) {
            case LOGICAL_AND -> (boolean) left && (boolean) right;
            case LOGICAL_OR -> (boolean) left || (boolean) right;
            case LOGICAL_EQUALITY -> left.equals(right);
            case LOGICAL_INEQUALITY -> !left.equals(right);
            case ADDITION -> (int) left + (int) right;
            case SUBTRACTION -> (int) left - (int) right;
            case DIVISION -> (int) left / (int) right;
            case MULTIPLICATION -> (int) left * (int) right;
            case LESS_THAN -> (int) left < (int) right;
            case LESS_EQUAL_THAN -> (int) left <= (int) right;
            case GREATER_THAN -> (int) left > (int) right;
            case GREATER_EQUAL_THAN -> (int) left >= (int) right;
            default -> throw new Exception("Unexpected binary operation " + b.getOperator());
        };
    }
}
