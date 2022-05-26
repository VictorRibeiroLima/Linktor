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

public class Binder {

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
        BoundUnaryOperatorKind kind = binUnaryOperatorKind(syntax.getKind());
        return new BoundUnaryExpression(kind, right);
    }

    private BoundExpression bindBinaryExpression(BinaryExpressionSyntax syntax) throws Exception {
        BoundExpression left = bindExpression(syntax.getLeft());
        BoundExpression right = bindExpression(syntax.getRight());
        BoundBinaryOperatorKind kind = binBinaryOperatorKind(syntax.getKind());
        return new BoundBinaryExpression(left, kind, right);
    }

    private BoundUnaryOperatorKind binUnaryOperatorKind(SyntaxKind kind) throws Exception {
        switch (kind) {
            case PLUS_TOKEN:
                return BoundUnaryOperatorKind.IDENTITY;
            case MINUS_TOKEN:
                return BoundUnaryOperatorKind.NEGATION;
            default:
                throw new Exception("ERROR: unexpected unary operator kind: " + kind);
        }
    }

    private BoundBinaryOperatorKind binBinaryOperatorKind(SyntaxKind kind) throws Exception {
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
