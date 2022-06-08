package codeanalysis.binding.expression.sufixpreffix;

import codeanalysis.symbol.TypeSymbol;
import codeanalysis.syntax.SyntaxKind;

import java.util.Arrays;
import java.util.List;

public final class BoundPrefixSuffixOperator {
    private final SyntaxKind syntaxKind;
    private final BoundPrefixSuffixOperatorKind kind;
    private final TypeSymbol operandType;
    private final TypeSymbol resultType;

    private static final List<BoundPrefixSuffixOperator> operators = Arrays.asList(
            new BoundPrefixSuffixOperator(SyntaxKind.MINUS_MINUS_TOKEN, BoundPrefixSuffixOperatorKind.DECREMENT, TypeSymbol.INTEGER),
            new BoundPrefixSuffixOperator(SyntaxKind.PLUS_PLUS_TOKEN, BoundPrefixSuffixOperatorKind.INCREMENT, TypeSymbol.INTEGER)

    );


    private BoundPrefixSuffixOperator(SyntaxKind syntaxKind, BoundPrefixSuffixOperatorKind kind, TypeSymbol operandType, TypeSymbol resultType) {
        this.syntaxKind = syntaxKind;
        this.kind = kind;
        this.operandType = operandType;
        this.resultType = resultType;
    }

    private BoundPrefixSuffixOperator(SyntaxKind syntaxKind, BoundPrefixSuffixOperatorKind kind, TypeSymbol operandType) {
        this(syntaxKind, kind, operandType, operandType);
    }

    public SyntaxKind getSyntaxKind() {
        return syntaxKind;
    }

    public BoundPrefixSuffixOperatorKind getKind() {
        return kind;
    }

    public TypeSymbol getOperandType() {
        return operandType;
    }

    public TypeSymbol getResultType() {
        return resultType;
    }

    public static BoundPrefixSuffixOperator bind(SyntaxKind syntaxKind, TypeSymbol operandType) {
        for (BoundPrefixSuffixOperator operator : operators) {
            if (operator.getSyntaxKind() == syntaxKind && operator.getOperandType() == operandType)
                return operator;
        }
        return null;
    }
}
