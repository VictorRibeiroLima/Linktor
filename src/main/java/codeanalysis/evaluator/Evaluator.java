package codeanalysis.evaluator;

import codeanalysis.binding.BoundProgram;
import codeanalysis.binding.conversion.BoundConversionExpression;
import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.binding.expression.assignment.BoundAssignmentExpression;
import codeanalysis.binding.expression.binary.BoundBinaryExpression;
import codeanalysis.binding.expression.call.BoundCallExpression;
import codeanalysis.binding.expression.literal.BoundLiteralExpression;
import codeanalysis.binding.expression.unary.BoundUnaryExpression;
import codeanalysis.binding.expression.variable.BoundVariableExpression;
import codeanalysis.binding.statement.BoundStatement;
import codeanalysis.binding.statement.block.BoundBlockStatement;
import codeanalysis.binding.statement.declaration.BoundLabelDeclarationStatement;
import codeanalysis.binding.statement.declaration.BoundVariableDeclarationStatement;
import codeanalysis.binding.statement.expression.BoundExpressionStatement;
import codeanalysis.binding.statement.expression.BoundReturnStatement;
import codeanalysis.binding.statement.jumpto.BoundConditionalJumpToStatement;
import codeanalysis.binding.statement.jumpto.BoundJumpToStatement;
import codeanalysis.binding.statement.jumpto.BoundLabel;
import codeanalysis.symbol.BuildInFunctions;
import codeanalysis.symbol.FunctionSymbol;
import codeanalysis.symbol.ParameterSymbol;
import codeanalysis.symbol.TypeSymbol;
import codeanalysis.symbol.variable.VariableSymbol;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

public final class Evaluator {
    private final BoundProgram root;

    private Object lastValue;

    private final Map<FunctionSymbol, BoundBlockStatement> functions = new HashMap<>();

    private final Stack<Map<VariableSymbol, Object>> callStack = new Stack<>();

    public Evaluator(BoundProgram root, Map<VariableSymbol, Object> variables) {
        this.root = root;
        callStack.add(variables);
        var current = root;
        while (current != null) {
            for (var fb : current.getFunctionsBodies().entrySet()) {
                var function = fb.getKey();
                var body = fb.getValue();
                functions.put(function, body);
            }
            current = current.getPrevious();
        }
    }

    public Object evaluate() throws Exception {
        return evaluateBlockStatement(root.getStatement());
    }

    private Object evaluateBlockStatement(BoundBlockStatement body) throws Exception {
        Map<BoundLabel, Integer> labelIndexes = mapLabels(body);
        int index = 0;
        while (index < body.getStatements().size()) {
            BoundStatement statement = body.getStatements().get(index);
            switch (statement.getKind()) {
                case EXPRESSION_STATEMENT -> {
                    evaluateExpressionStatement((BoundExpressionStatement) statement);
                    index++;
                }
                case VARIABLE_DECLARATION_STATEMENT -> {
                    evaluateVariableDeclarationStatement((BoundVariableDeclarationStatement) statement);
                    index++;
                }
                case JUMP_TO_STATEMENT -> {
                    BoundJumpToStatement jumpTo = (BoundJumpToStatement) statement;
                    index = labelIndexes.get(jumpTo.getLabel());
                }
                case CONDITIONAL_JUMP_TO_STATEMENT -> {
                    BoundConditionalJumpToStatement jumpTo = (BoundConditionalJumpToStatement) statement;
                    boolean condition = (boolean) evaluateExpression(jumpTo.getCondition());
                    if (condition == jumpTo.isJumpIfTrue())
                        index = labelIndexes.get(jumpTo.getLabel());
                    else
                        index++;
                }
                case RETURN_STATEMENT -> {
                    evaluateReturnStatement((BoundReturnStatement) statement);
                    return lastValue;
                }
                case LABEL_DECLARATION_STATEMENT -> index++;
                default -> throw new Exception("Unexpected node " + statement.getKind());
            }
        }
        return lastValue;
    }

    private void evaluateReturnStatement(BoundReturnStatement statement) throws Exception {
        if (statement.getExpression() != null)
            lastValue = evaluateExpression(statement.getExpression());
    }

    private Map<BoundLabel, Integer> mapLabels(BoundBlockStatement body) {
        Map<BoundLabel, Integer> labelIndexes = new HashMap<>();
        for (int i = 0; i < body.getStatements().size(); i++) {
            if (body.getStatements().get(i) instanceof BoundLabelDeclarationStatement l)
                labelIndexes.put(l.getLabel(), i);
        }
        return labelIndexes;
    }

    private void evaluateVariableDeclarationStatement(BoundVariableDeclarationStatement statement) throws Exception {
        Object value = evaluateExpression(statement.getInitializer());
        Map<VariableSymbol, Object> variables = callStack.peek();
        variables.put(statement.getVariable(), value);
        lastValue = value;
    }

    private void evaluateExpressionStatement(BoundExpressionStatement statement) throws Exception {
        lastValue = evaluateExpression(statement.getExpression());
    }

    private Object evaluateExpression(BoundExpression node) throws Exception {
        return switch (node.getKind()) {
            case LITERAL_EXPRESSION -> evaluateLiteralExpression((BoundLiteralExpression) node);
            case VARIABLE_EXPRESSION -> evaluateVariableExpression((BoundVariableExpression) node);
            case ASSIGNMENT_EXPRESSION -> evaluateAssignmentExpression((BoundAssignmentExpression) node);
            case UNARY_EXPRESSION -> evaluateUnaryExpression((BoundUnaryExpression) node);
            case BINARY_EXPRESSION -> evaluateBinaryExpression((BoundBinaryExpression) node);
            case CALL_EXPRESSION -> evaluateCallExpression((BoundCallExpression) node);
            case CONVERSION_EXPRESSION -> evaluateConversionExpression((BoundConversionExpression) node);
            default -> throw new Exception("Unexpected node " + node.getKind());
        };
    }

    private Object evaluateConversionExpression(BoundConversionExpression node) throws Exception {
        Object result = evaluateExpression(node.getExpression());
        TypeSymbol type = node.getType();
        if (type == TypeSymbol.BOOLEAN) {
            return Boolean.parseBoolean(result.toString());
        } else if (type == TypeSymbol.INTEGER) {
            return Integer.parseInt(result.toString());
        } else if (type == TypeSymbol.STRING) {
            return result.toString();
        } else
            throw new Exception("Unexpected type " + type);

    }

    private Object evaluateCallExpression(BoundCallExpression node) throws Exception {
        if (node.getFunction().equals(BuildInFunctions.READ)) {
            Scanner scanner = new Scanner(System.in);
            return scanner.nextLine();
        } else if (node.getFunction().equals(BuildInFunctions.PRINT)) {
            String message = String.valueOf(evaluateExpression(node.getArgs().get(0)));
            System.out.print(message);
            return null;
        } else if (node.getFunction().equals(BuildInFunctions.PRINTF)) {
            String message = String.valueOf(evaluateExpression(node.getArgs().get(0)));
            System.out.println(message);
            return null;
        } else if (node.getFunction().equals(BuildInFunctions.RANDOM)) {
            int max = (int) evaluateExpression(node.getArgs().get(0));
            return (int) Math.floor(Math.random() * (max + 1));
        } else {
            Map<VariableSymbol, Object> variables = new HashMap<>(callStack.get(0));
            for (int i = 0; i < node.getArgs().size(); i++) {
                ParameterSymbol param = node.getFunction().getParameters().get(i);
                Object value = evaluateExpression(node.getArgs().get(i));
                variables.put(param, value);
            }
            callStack.add(variables);
            BoundBlockStatement statement = functions.get(node.getFunction());
            Object result = evaluateBlockStatement(statement);
            callStack.pop();
            return result;
        }
    }

    private Object evaluateLiteralExpression(BoundLiteralExpression l) {
        return l.getValue();
    }

    private Object evaluateVariableExpression(BoundVariableExpression v) {
        Map<VariableSymbol, Object> variables = callStack.peek();
        return variables.get(v.getVariable());
    }

    private Object evaluateAssignmentExpression(BoundAssignmentExpression a) throws Exception {
        Map<VariableSymbol, Object> variables = callStack.peek();
        Object value = evaluateExpression(a.getBoundExpression());
        variables.put(a.getVariable(), value);
        return value;
    }

    private Object evaluateUnaryExpression(BoundUnaryExpression u) throws Exception {
        Object value = evaluateExpression(u.getRight());
        switch (u.getOperator().getKind()) {
            case IDENTITY:
                return value;
            case NEGATION:
                return -(int) value;
            case LOGICAL_NEGATION:
                return !(boolean) value;
            case ONES_COMPLEMENT: {
                if (u.getType().equals(TypeSymbol.BOOLEAN)) {
                    boolean isTrue = (boolean) value;
                    return isTrue ? ~1 : ~0;
                } else
                    return ~(int) value;
            }
            default:
                throw new Exception("Unexpected unary operation " + u.getOperator());
        }
    }

    private Object evaluateBinaryExpression(BoundBinaryExpression b) throws Exception {
        Object left = evaluateExpression(b.getLeft());
        Object right = evaluateExpression(b.getRight());
        switch (b.getOperator().getKind()) {
            case LOGICAL_AND:
                return (boolean) left && (boolean) right;
            case BITWISE_AND: {
                if (b.getType().equals(TypeSymbol.BOOLEAN))
                    return (boolean) left & (boolean) right;
                else
                    return (int) left & (int) right;
            }
            case LOGICAL_OR:
                return (boolean) left || (boolean) right;
            case BITWISE_OR: {
                if (b.getType().equals(TypeSymbol.BOOLEAN))
                    return (boolean) left | (boolean) right;
                else
                    return (int) left | (int) right;
            }
            case LOGICAL_EQUALITY:
                return left.equals(right);
            case LOGICAL_INEQUALITY:
                return !left.equals(right);

            case BITWISE_XOR: {
                if (b.getType().equals(TypeSymbol.BOOLEAN))
                    return (boolean) left ^ (boolean) right;
                else
                    return (int) left ^ (int) right;
            }
            case ADDITION:
                return (int) left + (int) right;
            case CONCATENATION:
                return left.toString() + right.toString();
            case SUBTRACTION:
                return (int) left - (int) right;
            case DIVISION:
                return (int) left / (int) right;
            case MOD:
                return (int) left % (int) right;
            case MULTIPLICATION:
                return (int) left * (int) right;
            case LESS_THAN:
                return (int) left < (int) right;
            case LESS_EQUAL_THAN:
                return (int) left <= (int) right;
            case GREATER_THAN:
                return (int) left > (int) right;
            case GREATER_EQUAL_THAN:
                return (int) left >= (int) right;
            default:
                throw new Exception("Unexpected binary operation " + b.getOperator());
        }
    }
}
