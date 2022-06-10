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
import codeanalysis.binding.statement.jumpto.BoundConditionalJumpToStatement;
import codeanalysis.binding.statement.jumpto.BoundJumpToStatement;
import codeanalysis.binding.statement.loop.BoundForConditionClause;
import codeanalysis.binding.statement.loop.BoundForStatement;
import codeanalysis.binding.statement.loop.BoundWhileStatement;
import codeanalysis.syntax.SyntaxFacts;
import util.ConsoleColors;

import java.io.PrintWriter;

public class BoundNodeWriter {
    private final PrintWriter out;
    private int indentation = 0;

    private BoundNodeWriter(PrintWriter out) {
        this.out = out;
    }

    public static void writeTo(PrintWriter out, BoundNode node) {
        BoundNodeWriter writer = new BoundNodeWriter(out);
        writer.writeTo(node);
    }

    private void writeTo(BoundNode node) {
        switch (node.getKind()) {
            case BLOCK_STATEMENT -> writeBlockStatement((BoundBlockStatement) node);
            case EXPRESSION_STATEMENT -> writeExpressionStatement((BoundExpressionStatement) node);
            case VARIABLE_DECLARATION_STATEMENT ->
                    writeVariableDeclarationStatement((BoundVariableDeclarationStatement) node);
            case IF_STATEMENT -> writeIfStatement((BoundIfStatement) node);
            case ELSE_CLAUSE -> writeElseClause((BoundElseClause) node);
            case WHILE_STATEMENT -> writeWhileStatement((BoundWhileStatement) node);
            case FOR_STATEMENT -> writeForStatement((BoundForStatement) node);
            case FOR_CONDITION_CLAUSE -> writeForConditionClause((BoundForConditionClause) node);
            case JUMP_TO_STATEMENT -> writeJumpToStatement((BoundJumpToStatement) node);
            case LABEL_DECLARATION_STATEMENT -> writeLabelDeclarationStatement((BoundLabelDeclarationStatement) node);
            case CONDITIONAL_JUMP_TO_STATEMENT ->
                    writeConditionalJumpToStatement((BoundConditionalJumpToStatement) node);
            case LITERAL_EXPRESSION -> writeLiteralExpression((BoundLiteralExpression) node);
            case UNARY_EXPRESSION -> writeUnaryExpression((BoundUnaryExpression) node);
            case VARIABLE_EXPRESSION -> writeVariableExpression((BoundVariableExpression) node);
            case ASSIGNMENT_EXPRESSION -> writeAssignmentExpression((BoundAssignmentExpression) node);
            case ERROR_EXPRESSION -> writeErrorExpression((BoundErrorExpression) node);
            case CALL_EXPRESSION -> writeCallExpression((BoundCallExpression) node);
            case CONVERSION_EXPRESSION -> writeConversionExpression((BoundConversionExpression) node);
            case SUFFIX_EXPRESSION -> writeSuffixExpression((BoundSuffixExpression) node);
            case PREFIX_EXPRESSION -> writePrefixExpression((BoundPrefixExpression) node);
            case COMPOUND_ASSIGNMENT_EXPRESSION ->
                    writeCompoundAssignmentExpression((BoundCompoundAssignmentExpression) node);
            case BINARY_EXPRESSION -> writeBinaryExpression((BoundBinaryExpression) node);
        }
    }

    private void writeBlockStatement(BoundBlockStatement node) {
        writePunctuation("{");
        indentation++;
        for (var statement : node.getStatements())
            writeTo(statement);
        indentation--;
        writeLine();
        writePunctuation("}");
        out.flush();
    }

    private void writeExpressionStatement(BoundExpressionStatement node) {
        writeLine();
        writeTo(node.getExpression());
    }

    private void writeVariableDeclarationStatement(BoundVariableDeclarationStatement node) {
        writeLine();
        var isReadonly = node.getVariable().isReadOnly();
        String keyword = isReadonly ? "let" : "var";
        writeKeyword(keyword);
        writeIdentifier(node.getVariable().getName());
        writePunctuation("=");
        writeTo(node.getInitializer());

    }

    private void writeIfStatement(BoundIfStatement node) {
        writeLine();
        writeKeyword("if");
        writePunctuation("(");
        writeTo(node.getCondition());
        writePunctuation(")");
        writeNestedStatement(node.getThenStatement());
        if (node.getElseClause() != null) {
            writeElseClause(node.getElseClause());
        }
    }

    private void writeElseClause(BoundElseClause node) {
        writeLine();
        writeKeyword("else");
        writeNestedStatement(node.getThenStatement());
    }

    private void writeWhileStatement(BoundWhileStatement node) {
        writeLine();
        writeKeyword("while");
        writePunctuation("(");
        writeTo(node.getCondition());
        writePunctuation(")");
        writeNestedStatement(node.getThenStatement());
    }

    private void writeForStatement(BoundForStatement node) {
        writeLine();
        writeKeyword("for");
        writeForConditionClause(node.getCondition());
        writeNestedStatement(node.getThenStatement());
    }

    private void writeForConditionClause(BoundForConditionClause node) {
        writePunctuation("(");
        writeTo(node.getVariable());
        writePunctuation(",");
        writeTo(node.getConditionExpression());
        writePunctuation(",");
        writeTo(node.getIncrementExpression());
        writePunctuation(")");
    }

    private void writeJumpToStatement(BoundJumpToStatement node) {
        writeLine();
        writeJumpTo("jump to");
        writeLabelName(node.getLabel().name());
    }

    private void writeLabelDeclarationStatement(BoundLabelDeclarationStatement node) {
        indentation--;
        writeLine();
        writeLabel(node.getLabel().name());
        indentation++;
    }

    private void writeConditionalJumpToStatement(BoundConditionalJumpToStatement node) {
        writeLine();
        writeJumpTo("jump to");
        writeLabelName(node.getLabel().name());
        if (node.isJumpIfTrue())
            writeKeyword("if");
        else
            writeKeyword("if not");
        writeTo(node.getCondition());
    }

    private void writeNestedStatement(BoundNode node) {
        var needIndentation = !(node instanceof BoundBlockStatement);
        if (needIndentation)
            indentation++;
        writeTo(node);
        if (needIndentation)
            indentation--;

    }

    private void writeLiteralExpression(BoundLiteralExpression node) {
        switch (node.getType().getName()) {
            case "string" -> writeString(node.getValue().toString());
            case "int" -> writeNumber(node.getValue().toString());
            case "boolean" -> writeKeyword(node.getValue().toString());
        }
    }

    private void writeUnaryExpression(BoundUnaryExpression node) {
        String punctuation = SyntaxFacts.getText(node.getOperator().getSyntaxKind());
        writePunctuation(punctuation);
        writeTo(node.getRight());
    }

    private void writeVariableExpression(BoundVariableExpression node) {
        writeIdentifier(node.getVariable().getName());
    }

    private void writeAssignmentExpression(BoundAssignmentExpression node) {
        writePunctuation("(");
        writeIdentifier(node.getVariable().getName());
        writePunctuation("=");
        writeTo(node.getBoundExpression());
        writePunctuation(")");
    }

    private void writeErrorExpression(BoundErrorExpression node) {
    }

    private void writeCallExpression(BoundCallExpression node) {
        writeFunctionName(node.getFunction().getName());
        writePunctuation("(");
        for (var arg : node.getArgs())
            writeTo(arg);
        writePunctuation(")");

    }

    private void writeConversionExpression(BoundConversionExpression node) {
        writeTo(node.getExpression());
    }

    private void writeSuffixExpression(BoundSuffixExpression node) {
        String punctuation = SyntaxFacts.getText(node.getOperator().getSyntaxKind());
        writeIdentifier(node.getLeft().getName());
        writePunctuation(punctuation);
    }

    private void writePrefixExpression(BoundPrefixExpression node) {
        String punctuation = SyntaxFacts.getText(node.getOperator().getSyntaxKind());
        writePunctuation(punctuation);
        writeIdentifier(node.getRight().getName());
    }

    private void writeCompoundAssignmentExpression(BoundCompoundAssignmentExpression node) {
        String punctuation = SyntaxFacts.getText(node.getOperator().getSyntaxKind());
        writeIdentifier(node.getVariable().getName());
        writePunctuation(punctuation);
        writeTo(node.getBoundExpression());
    }

    private void writeBinaryExpression(BoundBinaryExpression node) {
        String punctuation = SyntaxFacts.getText(node.getOperator().getSyntaxKind());
        writeTo(node.getLeft());
        writePunctuation(punctuation);
        writeTo(node.getRight());

    }

    private void writeLine() {
        String indent = "";
        indent = indent + "    ".repeat(indentation);
        out.println("");
        out.print(indent);
    }

    private void write(String text) {
        out.print(text + " ");
    }

    private void writeNumber(String text) {
        out.print(ConsoleColors.PURPLE_255);
        write(text);
        out.print(ConsoleColors.RESET);
    }

    private void writeString(String text) {
        out.print(ConsoleColors.YELLOW_BOLD);
        out.print("\"");
        out.print(text);
        write("\"");
        out.print(ConsoleColors.RESET);
    }

    private void writePunctuation(String text) {
        out.print(ConsoleColors.WHITE_BOLD);
        write(text);
        out.print(ConsoleColors.RESET);
    }

    private void writeLabelName(String text) {
        out.print(ConsoleColors.GREEN_255);
        write(text);
        out.print(ConsoleColors.RESET);
    }

    private void writeLabel(String text) {
        out.print(ConsoleColors.WHITE_BOLD);
        write(text + ":");
        out.print(ConsoleColors.RESET);
    }

    private void writeJumpTo(String text) {
        out.print(ConsoleColors.PINK_255);
        write(text);
        out.print(ConsoleColors.RESET);
    }

    private void writeFunctionName(String text) {
        out.print(ConsoleColors.ORANGE_255);
        write(text);
        out.print(ConsoleColors.RESET);
    }

    private void writeIdentifier(String text) {
        out.print(ConsoleColors.PURPLE_255);
        write(text);
        out.print(ConsoleColors.RESET);
    }

    private void writeKeyword(String text) {
        out.print(ConsoleColors.PINK_255);
        write(text);
        out.print(ConsoleColors.RESET);
    }
}
