package codeanalysis.binding.expression.binary;

import codeanalysis.syntax.SyntaxKind;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public final class BoundBinaryOperator {
    private final SyntaxKind syntaxKind;
    private final BoundBinaryOperatorKind kind;
    private final Type leftType;
    private final Type rightType;
    private final Type resultType;

    private static final List<BoundBinaryOperator> operators = Arrays.asList(
            new BoundBinaryOperator(SyntaxKind.PLUS_TOKEN, BoundBinaryOperatorKind.ADDITION, Integer.class),
            new BoundBinaryOperator(SyntaxKind.MINUS_TOKEN, BoundBinaryOperatorKind.SUBTRACTION, Integer.class),
            new BoundBinaryOperator(SyntaxKind.STAR_TOKEN, BoundBinaryOperatorKind.MULTIPLICATION, Integer.class),
            new BoundBinaryOperator(SyntaxKind.SLASH_TOKEN, BoundBinaryOperatorKind.DIVISION, Integer.class),

            new BoundBinaryOperator(SyntaxKind.AMPERSAND_AMPERSAND_TOKEN, BoundBinaryOperatorKind.LOGICAL_AND,
                    Boolean.class),
            new BoundBinaryOperator(SyntaxKind.PIPE_PIPE_TOKEN, BoundBinaryOperatorKind.LOGICAL_OR,
                    Boolean.class),
            new BoundBinaryOperator(SyntaxKind.EQUAL_EQUAL_TOKEN,
                    BoundBinaryOperatorKind.LOGICAL_EQUALITY, Integer.class, Boolean.class),
            new BoundBinaryOperator(SyntaxKind.EXCLAMATION_EQUAL_TOKEN,
                    BoundBinaryOperatorKind.LOGICAL_INEQUALITY, Integer.class, Boolean.class),
            new BoundBinaryOperator(SyntaxKind.EQUAL_EQUAL_TOKEN,
                    BoundBinaryOperatorKind.LOGICAL_EQUALITY, Boolean.class),
            new BoundBinaryOperator(SyntaxKind.EXCLAMATION_EQUAL_TOKEN,
                    BoundBinaryOperatorKind.LOGICAL_INEQUALITY, Boolean.class)
    );


    private BoundBinaryOperator(SyntaxKind syntaxKind, BoundBinaryOperatorKind kind,
                                Type leftType, Type rightType, Type resultType) {
        this.syntaxKind = syntaxKind;
        this.kind = kind;
        this.leftType = leftType;
        this.rightType = rightType;
        this.resultType = resultType;
    }

    private BoundBinaryOperator(SyntaxKind syntaxKind, BoundBinaryOperatorKind kind, Type operandsType, Type resultType) {
        this(syntaxKind, kind, operandsType, operandsType, resultType);
    }

    private BoundBinaryOperator(SyntaxKind syntaxKind, BoundBinaryOperatorKind kind, Type type) {
        this(syntaxKind, kind, type, type);
    }

    public SyntaxKind getSyntaxKind() {
        return syntaxKind;
    }

    public BoundBinaryOperatorKind getKind() {
        return kind;
    }

    public Type getLeftType() {
        return leftType;
    }

    public Type getResultType() {
        return resultType;
    }

    public Type getRightType() {
        return rightType;
    }

    public static BoundBinaryOperator bind(SyntaxKind syntaxKind, Type leftType, Type rightType) {
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
