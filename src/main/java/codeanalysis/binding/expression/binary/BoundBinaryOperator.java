package codeanalysis.binding.expression.binary;

import codeanalysis.symbol.TypeSymbol;
import codeanalysis.syntax.SyntaxKind;

import java.util.Arrays;
import java.util.List;

public final class BoundBinaryOperator {
    private final SyntaxKind syntaxKind;
    private final BoundBinaryOperatorKind kind;
    private final TypeSymbol leftType;
    private final TypeSymbol rightType;
    private final TypeSymbol resultType;

    private static final List<BoundBinaryOperator> operators = Arrays.asList(
            new BoundBinaryOperator(SyntaxKind.PLUS_TOKEN, BoundBinaryOperatorKind.CONCATENATION, TypeSymbol.STRING),
            new BoundBinaryOperator(SyntaxKind.PLUS_TOKEN, BoundBinaryOperatorKind.ADDITION, TypeSymbol.INTEGER),
            new BoundBinaryOperator(SyntaxKind.MINUS_TOKEN, BoundBinaryOperatorKind.SUBTRACTION, TypeSymbol.INTEGER),
            new BoundBinaryOperator(SyntaxKind.STAR_TOKEN, BoundBinaryOperatorKind.MULTIPLICATION, TypeSymbol.INTEGER),
            new BoundBinaryOperator(SyntaxKind.SLASH_TOKEN, BoundBinaryOperatorKind.DIVISION, TypeSymbol.INTEGER),
            new BoundBinaryOperator(SyntaxKind.HAT_TOKEN, BoundBinaryOperatorKind.BITWISE_XOR, TypeSymbol.INTEGER),
            new BoundBinaryOperator(SyntaxKind.PIPE_TOKEN, BoundBinaryOperatorKind.BITWISE_OR,
                    TypeSymbol.BOOLEAN),
            new BoundBinaryOperator(SyntaxKind.AMPERSAND_TOKEN, BoundBinaryOperatorKind.BITWISE_AND,
                    TypeSymbol.BOOLEAN),

            new BoundBinaryOperator(SyntaxKind.AMPERSAND_AMPERSAND_TOKEN, BoundBinaryOperatorKind.LOGICAL_AND,
                    TypeSymbol.BOOLEAN),
            new BoundBinaryOperator(SyntaxKind.AMPERSAND_TOKEN, BoundBinaryOperatorKind.BITWISE_AND,
                    TypeSymbol.BOOLEAN),
            new BoundBinaryOperator(SyntaxKind.PIPE_PIPE_TOKEN, BoundBinaryOperatorKind.LOGICAL_OR,
                    TypeSymbol.BOOLEAN),
            new BoundBinaryOperator(SyntaxKind.PIPE_TOKEN, BoundBinaryOperatorKind.BITWISE_OR,
                    TypeSymbol.BOOLEAN),
            new BoundBinaryOperator(SyntaxKind.AMPERSAND_TOKEN, BoundBinaryOperatorKind.BITWISE_AND,
                    TypeSymbol.INTEGER),
            new BoundBinaryOperator(SyntaxKind.PIPE_PIPE_TOKEN, BoundBinaryOperatorKind.LOGICAL_OR,
                    TypeSymbol.INTEGER),
            new BoundBinaryOperator(SyntaxKind.PIPE_TOKEN, BoundBinaryOperatorKind.BITWISE_OR,
                    TypeSymbol.INTEGER),
            new BoundBinaryOperator(SyntaxKind.EQUAL_EQUAL_TOKEN,
                    BoundBinaryOperatorKind.LOGICAL_EQUALITY, TypeSymbol.INTEGER, TypeSymbol.BOOLEAN),
            new BoundBinaryOperator(SyntaxKind.EXCLAMATION_EQUAL_TOKEN,
                    BoundBinaryOperatorKind.LOGICAL_INEQUALITY, TypeSymbol.INTEGER, TypeSymbol.BOOLEAN),
            new BoundBinaryOperator(SyntaxKind.EQUAL_EQUAL_TOKEN,
                    BoundBinaryOperatorKind.LOGICAL_EQUALITY, TypeSymbol.BOOLEAN),
            new BoundBinaryOperator(SyntaxKind.EXCLAMATION_EQUAL_TOKEN,
                    BoundBinaryOperatorKind.LOGICAL_INEQUALITY, TypeSymbol.BOOLEAN),
            new BoundBinaryOperator(SyntaxKind.HAT_TOKEN, BoundBinaryOperatorKind.BITWISE_XOR, TypeSymbol.INTEGER),
            new BoundBinaryOperator(SyntaxKind.HAT_TOKEN, BoundBinaryOperatorKind.BITWISE_XOR, TypeSymbol.BOOLEAN),

            new BoundBinaryOperator(SyntaxKind.LESS_TOKEN,
                    BoundBinaryOperatorKind.LESS_THAN, TypeSymbol.INTEGER, TypeSymbol.BOOLEAN),
            new BoundBinaryOperator(SyntaxKind.LESS_EQUAL_TOKEN,
                    BoundBinaryOperatorKind.LESS_EQUAL_THAN, TypeSymbol.INTEGER, TypeSymbol.BOOLEAN),
            new BoundBinaryOperator(SyntaxKind.GREATER_TOKEN,
                    BoundBinaryOperatorKind.GREATER_THAN, TypeSymbol.INTEGER, TypeSymbol.BOOLEAN),
            new BoundBinaryOperator(SyntaxKind.GREATER_EQUAL_TOKEN,
                    BoundBinaryOperatorKind.GREATER_EQUAL_THAN, TypeSymbol.INTEGER, TypeSymbol.BOOLEAN)
    );


    private BoundBinaryOperator(SyntaxKind syntaxKind, BoundBinaryOperatorKind kind,
                                TypeSymbol leftType, TypeSymbol rightType, TypeSymbol resultType) {
        this.syntaxKind = syntaxKind;
        this.kind = kind;
        this.leftType = leftType;
        this.rightType = rightType;
        this.resultType = resultType;
    }

    private BoundBinaryOperator(SyntaxKind syntaxKind, BoundBinaryOperatorKind kind, TypeSymbol operandsType, TypeSymbol resultType) {
        this(syntaxKind, kind, operandsType, operandsType, resultType);
    }

    private BoundBinaryOperator(SyntaxKind syntaxKind, BoundBinaryOperatorKind kind, TypeSymbol type) {
        this(syntaxKind, kind, type, type);
    }

    public SyntaxKind getSyntaxKind() {
        return syntaxKind;
    }

    public BoundBinaryOperatorKind getKind() {
        return kind;
    }

    public TypeSymbol getLeftType() {
        return leftType;
    }

    public TypeSymbol getResultType() {
        return resultType;
    }

    public TypeSymbol getRightType() {
        return rightType;
    }

    public static BoundBinaryOperator bind(SyntaxKind syntaxKind, TypeSymbol leftType, TypeSymbol rightType) {
        for (BoundBinaryOperator operator : operators) {
            if (
                    operator.getSyntaxKind() == syntaxKind &&
                            operator.getLeftType() == leftType &&
                            operator.getRightType() == rightType
            )
                return operator;
        }
        return null;
    }
}
