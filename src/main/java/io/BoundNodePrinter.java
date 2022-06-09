package io;

import codeanalysis.binding.BoundNode;
import codeanalysis.binding.conversion.BoundConversionExpression;
import codeanalysis.binding.expression.assignment.BoundAssignmentExpression;
import codeanalysis.binding.expression.assignment.BoundCompoundAssignmentExpression;
import codeanalysis.binding.expression.binary.BoundBinaryExpression;
import codeanalysis.binding.expression.call.BoundCallExpression;
import codeanalysis.binding.expression.error.BoundErrorExpression;
import codeanalysis.binding.expression.literal.BoundLiteralExpression;
import codeanalysis.binding.expression.sufixpreffix.BoundPrefixExpression;
import codeanalysis.binding.expression.sufixpreffix.BoundSuffixExpression;
import codeanalysis.binding.expression.unary.BoundUnaryExpression;
import codeanalysis.binding.expression.variable.BoundVariableExpression;
import codeanalysis.binding.statement.block.BoundBlockStatement;
import codeanalysis.binding.statement.conditional.BoundElseClause;
import codeanalysis.binding.statement.conditional.BoundIfStatement;
import codeanalysis.binding.statement.declaration.BoundLabelDeclarationStatement;
import codeanalysis.binding.statement.declaration.BoundVariableDeclarationStatement;
import codeanalysis.binding.statement.expression.BoundExpressionStatement;
import codeanalysis.binding.statement.jumpto.BoundJumpToStatement;
import codeanalysis.binding.statement.loop.BoundForConditionClause;
import codeanalysis.binding.statement.loop.BoundForStatement;
import codeanalysis.binding.statement.loop.BoundWhileStatement;

import java.io.PrintWriter;

public class BoundNodePrinter {
    private BoundNodePrinter() {
    }

    public static void writeTo(PrintWriter out, BoundNode node) {
        switch (node.getKind()) {
            case LITERAL_EXPRESSION -> writeLiteralExpression(out, (BoundLiteralExpression) node);
            case UNARY_EXPRESSION -> writeLUnaryExpression(out, (BoundUnaryExpression) node);
            case VARIABLE_EXPRESSION -> writeVariableExpression(out, (BoundVariableExpression) node);
            case ASSIGNMENT_EXPRESSION -> writeAssignmentExpression(out, (BoundAssignmentExpression) node);
            case ERROR_EXPRESSION -> writeErrorExpression(out, (BoundErrorExpression) node);
            case CALL_EXPRESSION -> writeCallExpression(out, (BoundCallExpression) node);
            case CONVERSION_EXPRESSION -> writeConversionExpression(out, (BoundConversionExpression) node);
            case SUFFIX_EXPRESSION -> writeSuffixExpression(out, (BoundSuffixExpression) node);
            case PREFIX_EXPRESSION -> writePrefixExpression(out, (BoundPrefixExpression) node);
            case COMPOUND_ASSIGNMENT_EXPRESSION ->
                    writeCompoundAssignmentExpression(out, (BoundCompoundAssignmentExpression) node);
            case BINARY_EXPRESSION -> writeBinaryExpression(out, (BoundBinaryExpression) node);

            case BLOCK_STATEMENT -> writeBlockStatement(out, (BoundBlockStatement) node);
            case EXPRESSION_STATEMENT -> writeExpressionStatement(out, (BoundExpressionStatement) node);
            case VARIABLE_DECLARATION_STATEMENT ->
                    writeVariableDeclarationStatement(out, (BoundVariableDeclarationStatement) node);
            case IF_STATEMENT -> writeIfStatement(out, (BoundIfStatement) node);
            case ELSE_CLAUSE -> writeElseClause(out, (BoundElseClause) node);
            case WHILE_STATEMENT -> writeWhileStatement(out, (BoundWhileStatement) node);
            case FOR_STATEMENT -> writeForStatement(out, (BoundForStatement) node);
            case FOR_CONDITION_CLAUSE -> writeForConditionClause(out, (BoundForConditionClause) node);
            case JUMP_TO_STATEMENT -> writeJumpToStatement(out, (BoundJumpToStatement) node);
            case LABEL_DECLARATION_STATEMENT ->
                    writeLabelDeclarationStatement(out, (BoundLabelDeclarationStatement) node);
            case CONDITIONAL_JUMP_TO_STATEMENT ->
                    writeConditionalJumpToStatement(out, (BoundCompoundAssignmentExpression) node);

        }
    }

    private static void writeLiteralExpression(PrintWriter out, BoundLiteralExpression node) {
    }

    private static void writeLUnaryExpression(PrintWriter out, BoundUnaryExpression node) {
    }

    private static void writeVariableExpression(PrintWriter out, BoundVariableExpression node) {
    }

    private static void writeAssignmentExpression(PrintWriter out, BoundAssignmentExpression node) {
    }

    private static void writeErrorExpression(PrintWriter out, BoundErrorExpression node) {
    }

    private static void writeCallExpression(PrintWriter out, BoundCallExpression node) {
    }

    private static void writeConversionExpression(PrintWriter out, BoundConversionExpression node) {
    }

    private static void writeSuffixExpression(PrintWriter out, BoundSuffixExpression node) {
    }

    private static void writePrefixExpression(PrintWriter out, BoundPrefixExpression node) {
    }

    private static void writeCompoundAssignmentExpression(PrintWriter out, BoundCompoundAssignmentExpression node) {
    }

    private static void writeBinaryExpression(PrintWriter out, BoundBinaryExpression node) {
    }

    private static void writeBlockStatement(PrintWriter out, BoundBlockStatement node) {
    }

    private static void writeExpressionStatement(PrintWriter out, BoundExpressionStatement node) {
    }

    private static void writeVariableDeclarationStatement(PrintWriter out, BoundVariableDeclarationStatement node) {
    }

    private static void writeIfStatement(PrintWriter out, BoundIfStatement node) {
    }

    private static void writeElseClause(PrintWriter out, BoundElseClause node) {
    }

    private static void writeWhileStatement(PrintWriter out, BoundWhileStatement node) {
    }

    private static void writeForStatement(PrintWriter out, BoundForStatement node) {
    }

    private static void writeForConditionClause(PrintWriter out, BoundForConditionClause node) {
    }

    private static void writeJumpToStatement(PrintWriter out, BoundJumpToStatement node) {
    }

    private static void writeLabelDeclarationStatement(PrintWriter out, BoundLabelDeclarationStatement node) {
    }

    private static void writeConditionalJumpToStatement(PrintWriter out, BoundCompoundAssignmentExpression node) {
    }
}
