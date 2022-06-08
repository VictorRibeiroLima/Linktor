package codeanalysis.binding.expression.preffix;

import codeanalysis.symbol.TypeSymbol;
import codeanalysis.syntax.SyntaxKind;

import java.util.Arrays;
import java.util.List;

public final class BoundSuffixOperator {
    private final SyntaxKind syntaxKind;
    private final BoundSuffixOperatorKind kind;
    private final TypeSymbol operandType;
    private final TypeSymbol resultType;

    private static final List<BoundSuffixOperator> operators = Arrays.asList(
            new BoundSuffixOperator(SyntaxKind.MINUS_MINUS_TOKEN, BoundSuffixOperatorKind.DECREMENT, TypeSymbol.INTEGER),
            new BoundSuffixOperator(SyntaxKind.PLUS_PLUS_TOKEN, BoundSuffixOperatorKind.INCREMENT, TypeSymbol.INTEGER)

    );


    private BoundSuffixOperator(SyntaxKind syntaxKind, BoundSuffixOperatorKind kind, TypeSymbol operandType, TypeSymbol resultType) {
        this.syntaxKind = syntaxKind;
        this.kind = kind;
        this.operandType = operandType;
        this.resultType = resultType;
    }

    private BoundSuffixOperator(SyntaxKind syntaxKind, BoundSuffixOperatorKind kind, TypeSymbol operandType) {
        this(syntaxKind, kind, operandType, operandType);
    }

    public SyntaxKind getSyntaxKind() {
        return syntaxKind;
    }

    public BoundSuffixOperatorKind getKind() {
        return kind;
    }

    public TypeSymbol getOperandType() {
        return operandType;
    }

    public TypeSymbol getResultType() {
        return resultType;
    }

    public static BoundSuffixOperator bind(SyntaxKind syntaxKind, TypeSymbol operandType) {
        for (BoundSuffixOperator operator : operators) {
            if (operator.getSyntaxKind() == syntaxKind && operator.getOperandType() == operandType)
                return operator;
        }
        return null;
    }
}
