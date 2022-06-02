package codeanalysis.binding.expression.unary;

import codeanalysis.syntax.SyntaxKind;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public final class BoundUnaryOperator {
    private final SyntaxKind syntaxKind;
    private final BoundUnaryOperatorKind kind;
    private final Type operandType;
    private final Type resultType;

    private static final List<BoundUnaryOperator> operators = Arrays.asList(
            new BoundUnaryOperator(SyntaxKind.EXCLAMATION_TOKEN, BoundUnaryOperatorKind.LOGICAL_NEGATION, Boolean.class),
            new BoundUnaryOperator(SyntaxKind.MINUS_TOKEN, BoundUnaryOperatorKind.NEGATION, Integer.class),
            new BoundUnaryOperator(SyntaxKind.PLUS_TOKEN, BoundUnaryOperatorKind.IDENTITY, Integer.class),
            new BoundUnaryOperator(SyntaxKind.TILDE_TOKEN, BoundUnaryOperatorKind.ONES_COMPLEMENT, Integer.class),
            new BoundUnaryOperator(SyntaxKind.TILDE_TOKEN, BoundUnaryOperatorKind.ONES_COMPLEMENT, Boolean.class)

    );


    private BoundUnaryOperator(SyntaxKind syntaxKind, BoundUnaryOperatorKind kind, Type operandType, Type resultType) {
        this.syntaxKind = syntaxKind;
        this.kind = kind;
        this.operandType = operandType;
        this.resultType = resultType;
    }

    private BoundUnaryOperator(SyntaxKind syntaxKind, BoundUnaryOperatorKind kind, Type operandType) {
        this(syntaxKind, kind, operandType, operandType);
    }

    public SyntaxKind getSyntaxKind() {
        return syntaxKind;
    }

    public BoundUnaryOperatorKind getKind() {
        return kind;
    }

    public Type getOperandType() {
        return operandType;
    }

    public Type getResultType() {
        return resultType;
    }

    public static BoundUnaryOperator bind(SyntaxKind syntaxKind, Type operandType) {
        for (BoundUnaryOperator operator : operators) {
            if (operator.getSyntaxKind() == syntaxKind && operator.getOperandType() == operandType)
                return operator;
        }
        return null;
    }
}
