package codeanalysis.binding.expression.unary;

import codeanalysis.symbol.TypeSymbol;
import codeanalysis.syntax.SyntaxKind;

import java.util.Arrays;
import java.util.List;

public final class BoundUnaryOperator {
    private final SyntaxKind syntaxKind;
    private final BoundUnaryOperatorKind kind;
    private final TypeSymbol operandType;
    private final TypeSymbol resultType;

    private static final List<BoundUnaryOperator> operators = Arrays.asList(
            new BoundUnaryOperator(SyntaxKind.EXCLAMATION_TOKEN, BoundUnaryOperatorKind.LOGICAL_NEGATION, TypeSymbol.BOOLEAN),
            new BoundUnaryOperator(SyntaxKind.MINUS_TOKEN, BoundUnaryOperatorKind.NEGATION, TypeSymbol.INTEGER),
            new BoundUnaryOperator(SyntaxKind.PLUS_TOKEN, BoundUnaryOperatorKind.IDENTITY, TypeSymbol.INTEGER),
            new BoundUnaryOperator(SyntaxKind.TILDE_TOKEN, BoundUnaryOperatorKind.ONES_COMPLEMENT, TypeSymbol.INTEGER),
            new BoundUnaryOperator(SyntaxKind.TILDE_TOKEN, BoundUnaryOperatorKind.ONES_COMPLEMENT, TypeSymbol.BOOLEAN)

    );


    private BoundUnaryOperator(SyntaxKind syntaxKind, BoundUnaryOperatorKind kind, TypeSymbol operandType, TypeSymbol resultType) {
        this.syntaxKind = syntaxKind;
        this.kind = kind;
        this.operandType = operandType;
        this.resultType = resultType;
    }

    private BoundUnaryOperator(SyntaxKind syntaxKind, BoundUnaryOperatorKind kind, TypeSymbol operandType) {
        this(syntaxKind, kind, operandType, operandType);
    }

    public SyntaxKind getSyntaxKind() {
        return syntaxKind;
    }

    public BoundUnaryOperatorKind getKind() {
        return kind;
    }

    public TypeSymbol getOperandType() {
        return operandType;
    }

    public TypeSymbol getResultType() {
        return resultType;
    }

    public static BoundUnaryOperator bind(SyntaxKind syntaxKind, TypeSymbol operandType) {
        for (BoundUnaryOperator operator : operators) {
            if (operator.getSyntaxKind() == syntaxKind && operator.getOperandType() == operandType)
                return operator;
        }
        return null;
    }
}
