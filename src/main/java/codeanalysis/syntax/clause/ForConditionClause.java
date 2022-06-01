package codeanalysis.syntax.clause;

import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxNode;
import codeanalysis.syntax.expression.ExpressionSyntax;

import java.util.List;

public class ForConditionClause extends SyntaxNode {

    private final SyntaxNode variableNode;
    private final ExpressionSyntax conditionExpression;
    private final ExpressionSyntax incrementExpression;
    private final SyntaxKind kind;
    private final List<SyntaxNode> children;

    public ForConditionClause(SyntaxNode variableNode, ExpressionSyntax conditionExpression, ExpressionSyntax incrementExpression) {
        this.variableNode = variableNode;
        this.conditionExpression = conditionExpression;
        this.incrementExpression = incrementExpression;
        this.kind = SyntaxKind.FOR_CONDITION_CLAUSE;
        this.children = List.of(variableNode, conditionExpression, conditionExpression);
    }

    public SyntaxNode getVariableNode() {
        return variableNode;
    }

    public ExpressionSyntax getConditionExpression() {
        return conditionExpression;
    }

    public ExpressionSyntax getIncrementExpression() {
        return incrementExpression;
    }

    @Override
    public SyntaxKind getKind() {
        return kind;
    }

    @Override
    public List<SyntaxNode> getChildren() {
        return children;
    }
}
