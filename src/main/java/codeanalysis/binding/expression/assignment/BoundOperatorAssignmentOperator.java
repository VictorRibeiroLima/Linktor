package codeanalysis.binding.expression.assignment;

import codeanalysis.symbol.TypeSymbol;
import codeanalysis.syntax.SyntaxKind;

import java.util.Arrays;
import java.util.List;

public final class BoundOperatorAssignmentOperator {
    private final SyntaxKind syntaxKind;
    private final BoundOperationAssignmentOperatorKind kind;
    private final TypeSymbol operandType;
    private final TypeSymbol resultType;

    private static final List<BoundOperatorAssignmentOperator> operators = Arrays.asList(
            new BoundOperatorAssignmentOperator(SyntaxKind.MINUS_EQUALS_TOKEN, BoundOperationAssignmentOperatorKind.DECREMENT, TypeSymbol.INTEGER),
            new BoundOperatorAssignmentOperator(SyntaxKind.PLUS_EQUALS_TOKEN, BoundOperationAssignmentOperatorKind.INCREMENT, TypeSymbol.INTEGER),
            new BoundOperatorAssignmentOperator(SyntaxKind.PLUS_EQUALS_TOKEN, BoundOperationAssignmentOperatorKind.CONCATENATION, TypeSymbol.STRING)

    );


    private BoundOperatorAssignmentOperator(SyntaxKind syntaxKind, BoundOperationAssignmentOperatorKind kind, TypeSymbol operandType, TypeSymbol resultType) {
        this.syntaxKind = syntaxKind;
        this.kind = kind;
        this.operandType = operandType;
        this.resultType = resultType;
    }

    private BoundOperatorAssignmentOperator(SyntaxKind syntaxKind, BoundOperationAssignmentOperatorKind kind, TypeSymbol operandType) {
        this(syntaxKind, kind, operandType, operandType);
    }

    public SyntaxKind getSyntaxKind() {
        return syntaxKind;
    }

    public BoundOperationAssignmentOperatorKind getKind() {
        return kind;
    }

    public TypeSymbol getOperandType() {
        return operandType;
    }

    public TypeSymbol getResultType() {
        return resultType;
    }

    public static BoundOperatorAssignmentOperator bind(SyntaxKind syntaxKind, TypeSymbol operandType) {
        for (BoundOperatorAssignmentOperator operator : operators) {
            if (operator.getSyntaxKind() == syntaxKind && operator.getOperandType() == operandType)
                return operator;
        }
        return null;
    }
}
