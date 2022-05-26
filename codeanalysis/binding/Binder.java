package codeanalysis.binding;

import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.binding.expression.binary.BoundBinaryExpression;
import codeanalysis.binding.expression.binary.BoundBinaryOperatorKind;
import codeanalysis.binding.expression.literal.BoundLiteralExpression;
import codeanalysis.binding.expression.unary.BoundUnaryExpression;
import codeanalysis.binding.expression.unary.BoundUnaryOperatorKind;
import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.expression.BinaryExpressionSyntax;
import codeanalysis.syntax.expression.ExpressionSyntax;
import codeanalysis.syntax.expression.LiteralExpressionSyntax;
import codeanalysis.syntax.expression.UnaryExpressionSyntax;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Binder {

    private final List<String> diagnostics = new ArrayList<>();

    public List<String> getDiagnostics() {
        return diagnostics;
    }

    public BoundExpression bindExpression(ExpressionSyntax syntax) throws Exception {
        switch (syntax.getKind()) {
            case LITERAL_EXPRESSION:
                return bindLiteralExpression((LiteralExpressionSyntax) syntax);
            case UNARY_EXPRESSION:
                return bindUnaryExpression((UnaryExpressionSyntax) syntax);
            case BINARY_EXPRESSION:
                return bindBinaryExpression((BinaryExpressionSyntax) syntax);
            default:
                throw new Exception("ERROR: unexpected syntax: " + syntax.getKind());
        }
    }

    private BoundExpression bindLiteralExpression(LiteralExpressionSyntax syntax) {
        Object value = syntax.getToken().getValue() == null ? syntax.getToken().getValue() : 0;
        return new BoundLiteralExpression(value);
    }

    private BoundExpression bindUnaryExpression(UnaryExpressionSyntax syntax) throws Exception {
        BoundExpression right = bindExpression(syntax.getRight());
        BoundUnaryOperatorKind kind = binUnaryOperatorKind(syntax.getKind(), right.getType());
        if (kind == null) {
            diagnostics.add(
                    "ERROR: Unary operator " + syntax.getOperatorToken().getText() +
                            " is not defined for type " + right.getType() + "."
            );
            return right;
        }
        return new BoundUnaryExpression(kind, right);
    }

    private BoundExpression bindBinaryExpression(BinaryExpressionSyntax syntax) throws Exception {
        BoundExpression left = bindExpression(syntax.getLeft());
        BoundExpression right = bindExpression(syntax.getRight());
        BoundBinaryOperatorKind kind = binBinaryOperatorKind(syntax.getKind(), left.getType(), right.getType());
        if (kind == null) {
            diagnostics.add(
                    "ERROR: Binary operator " + syntax.getOperatorToken().getText() +
                            " is not defined for type " + left.getType() + " or " + right.getType() + "."
            );
            return left;
        }
        return new BoundBinaryExpression(left, kind, right);
    }

    private BoundUnaryOperatorKind binUnaryOperatorKind(SyntaxKind kind, Type boundType) throws Exception {
        if (!boundType.equals(Integer.class))
            return null;
        switch (kind) {
            case PLUS_TOKEN:
                return BoundUnaryOperatorKind.IDENTITY;
            case MINUS_TOKEN:
                return BoundUnaryOperatorKind.NEGATION;
            default:
                throw new Exception("ERROR: unexpected unary operator kind: " + kind);
        }
    }

    private BoundBinaryOperatorKind binBinaryOperatorKind(SyntaxKind kind, Type boundLeftType, Type boundRightType) throws Exception {
        if (!boundLeftType.equals(Integer.class) || !boundRightType.equals(Integer.class))
            return null;
        switch (kind) {
            case PLUS_TOKEN:
                return BoundBinaryOperatorKind.ADDITION;
            case MINUS_TOKEN:
                return BoundBinaryOperatorKind.SUBTRACTION;
            case STAR_TOKEN:
                return BoundBinaryOperatorKind.MULTIPLICATION;
            case SLASH_TOKEN:
                return BoundBinaryOperatorKind.DIVISION;
            default:
                throw new Exception("ERROR: unexpected binary operator kind: " + kind);
        }
    }
}
