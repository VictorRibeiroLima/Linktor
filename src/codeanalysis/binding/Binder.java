package src.codeanalysis.binding;

import src.codeanalysis.binding.expression.BoundExpression;
import src.codeanalysis.binding.expression.binary.BoundBinaryExpression;
import src.codeanalysis.binding.expression.binary.BoundBinaryOperator;
import src.codeanalysis.binding.expression.literal.BoundLiteralExpression;
import src.codeanalysis.binding.expression.unary.BoundUnaryExpression;
import src.codeanalysis.binding.expression.unary.BoundUnaryOperator;
import src.codeanalysis.diagnostics.DiagnosticBag;
import src.codeanalysis.syntax.expression.*;

public class Binder {

    private final DiagnosticBag diagnostics = new DiagnosticBag();

    public DiagnosticBag getDiagnostics() {
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
            diagnostics.reportUndefinedUnaryOperator(syntax.getOperatorToken().getSpan(),
                    syntax.getOperatorToken().getText(), right.getType());
            return right;
        }
        return new BoundUnaryExpression(operator, right);
    }

    private BoundExpression bindBinaryExpression(BinaryExpressionSyntax syntax) throws Exception {
        BoundExpression left = bindExpression(syntax.getLeft());
        BoundExpression right = bindExpression(syntax.getRight());
        BoundBinaryOperator operator = BoundBinaryOperator.bind(syntax.getOperatorToken().getKind(), left.getType(), right.getType());
        if (operator == null) {
            diagnostics.reportUndefinedBinaryOperator(syntax.getOperatorToken().getSpan(),
                    syntax.getOperatorToken().getText(), left.getType(), right.getType());

            return left;
        }
        return new BoundBinaryExpression(left, operator, right);
    }
}
