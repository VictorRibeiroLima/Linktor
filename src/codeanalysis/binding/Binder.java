package src.codeanalysis.binding;

import src.codeanalysis.binding.expression.BoundExpression;
import src.codeanalysis.binding.expression.binary.BoundBinaryExpression;
import src.codeanalysis.binding.expression.binary.BoundBinaryOperator;
import src.codeanalysis.binding.expression.literal.BoundLiteralExpression;
import src.codeanalysis.binding.expression.unary.BoundUnaryExpression;
import src.codeanalysis.binding.expression.unary.BoundUnaryOperator;
import src.codeanalysis.syntax.expression.*;

import java.util.ArrayList;
import java.util.List;

public class Binder {

    private final List<String> diagnostics = new ArrayList<>();

    public List<String> getDiagnostics() {
        return diagnostics;
    }

    public BoundExpression bindExpression(ExpressionSyntax syntax) throws Exception {
        return switch (syntax.getKind()) {
            case LITERAL_EXPRESSION -> bindLiteralExpression((LiteralExpressionSyntax) syntax);
            case UNARY_EXPRESSION -> bindUnaryExpression((UnaryExpressionSyntax) syntax);
            case BINARY_EXPRESSION -> bindBinaryExpression((BinaryExpressionSyntax) syntax);
            case PARENTHESIZED_EXPRESSION -> bindExpression(((ParenthesizedExpressionSyntax) syntax).getExpression());
            default -> throw new Exception("ERROR: unexpected syntax: " + syntax.getKind());
        };
    }

    private BoundExpression bindLiteralExpression(LiteralExpressionSyntax syntax) {
        Object value = syntax.getValue() != null ? syntax.getValue() : 0;
        return new BoundLiteralExpression(value);
    }

    private BoundExpression bindUnaryExpression(UnaryExpressionSyntax syntax) throws Exception {
        BoundExpression right = bindExpression(syntax.getRight());
        BoundUnaryOperator operator = BoundUnaryOperator.bind(syntax.getOperatorToken().getKind(), right.getType());
        if (operator == null) {
            diagnostics.add(
                    "ERROR: Unary operator " + syntax.getOperatorToken().getText() +
                            " is not defined for type " + right.getType() + "."
            );
            return right;
        }
        return new BoundUnaryExpression(operator, right);
    }

    private BoundExpression bindBinaryExpression(BinaryExpressionSyntax syntax) throws Exception {
        BoundExpression left = bindExpression(syntax.getLeft());
        BoundExpression right = bindExpression(syntax.getRight());
        BoundBinaryOperator operator = BoundBinaryOperator.bind(syntax.getOperatorToken().getKind(), left.getType(), right.getType());
        if (operator == null) {
            diagnostics.add(
                    "ERROR: Binary operator " + syntax.getOperatorToken().getText() +
                            " is not defined for type " + left.getType() + " and " + right.getType() + "."
            );
            return left;
        }
        return new BoundBinaryExpression(left, operator, right);
    }
}
