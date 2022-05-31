package codeanalysis.evaluator;

import codeanalysis.binding.Binder;
import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.binding.scopes.BoundGlobalScope;
import codeanalysis.symbol.VariableSymbol;
import codeanalysis.syntax.SyntaxTree;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EvaluatorTest {

    static BoundGlobalScope globalScope = null;

    static Map<VariableSymbol, Object> variables;

    @BeforeAll
    static void setUp() {
        variables = new HashMap<>();
    }

    @ParameterizedTest
    @MethodSource("provideExpressions")
    void evaluate(BoundExpression expression, Object expectedResult) throws Exception {
        Evaluator evaluator = new Evaluator(expression, variables);
        Object result = evaluator.evaluate();
        assertEquals(expectedResult, result);
    }

    static Stream<Arguments> provideExpressions() throws Exception {

        return Stream.of(
                Arguments.of(getExpression("1"), 1),
                Arguments.of(getExpression("1+1"), 2),
                Arguments.of(getExpression("3-2"), 1),
                Arguments.of(getExpression("2-3"), -1),
                Arguments.of(getExpression("-3+2"), -1),
                Arguments.of(getExpression("-3-2"), -5),
                Arguments.of(getExpression("2*2"), 4),
                Arguments.of(getExpression("-2*2"), -4),
                Arguments.of(getExpression("2*-2"), -4),
                Arguments.of(getExpression("-2*-2"), 4),
                Arguments.of(getExpression("4/2"), 2),
                Arguments.of(getExpression("2+2*3"), 8),
                Arguments.of(getExpression("2*3+2"), 8),
                Arguments.of(getExpression("2*(3+2)"), 10),
                Arguments.of(getExpression("2*-1*(6-4)"), -4),
                Arguments.of(getExpression("-5 *4-(24/2-(5+2*3)+8)"), -29),
                Arguments.of(getExpression("false"), false),
                Arguments.of(getExpression("true"), true),
                Arguments.of(getExpression("!true"), false),
                Arguments.of(getExpression("!false"), true),
                Arguments.of(getExpression("!!false"), false),
                Arguments.of(getExpression("!!true"), true),
                Arguments.of(getExpression("-10"), -10),
                Arguments.of(getExpression("+-10"), -10),
                Arguments.of(getExpression("-+-10"), 10),
                Arguments.of(getExpression("true && true"), true),
                Arguments.of(getExpression("true && false"), false),
                Arguments.of(getExpression("false && true"), false),
                Arguments.of(getExpression("false && false"), false),
                Arguments.of(getExpression("false || false"), false),
                Arguments.of(getExpression("false || true"), true),
                Arguments.of(getExpression("true || false"), true),
                Arguments.of(getExpression("true || true"), true),
                Arguments.of(getExpression("!true || !true"), false),
                Arguments.of(getExpression("-5 *4-(24/2-(5+2*3)+8) == -29"), true),
                Arguments.of(getExpression("-5 *4-(24/2-(5+2*3)+8) == 29"), false),
                Arguments.of(getExpression("-5 *4-(24/2-(5+2*3)+8) == 29 || 1+1 ==2"), true),
                Arguments.of(getExpression("-5 *4-(24/2-(5+2*3)+8) == 29 && 1+1 ==2"), false),
                Arguments.of(getExpression("a = -5 *4-(24/2-(5+2*3)+8) == 29 && 1+1 ==2"), false),
                Arguments.of(getExpression("!a"), true),
                Arguments.of(getExpression("!!a"), false),
                Arguments.of(getExpression("b=10"), 10),
                Arguments.of(getExpression("c=25"), 25),
                Arguments.of(getExpression("b+c"), 35)
        );
    }

    static BoundExpression getExpression(String input) throws Exception {
        SyntaxTree tree = SyntaxTree.parse(input);
        tree.getRoot().writeTo(new PrintWriter(System.out, true));
        BoundGlobalScope localGlobalScope = Binder.boundGlobalScope(tree.getRoot(), globalScope);
        globalScope = localGlobalScope;
        BoundExpression bound = localGlobalScope.getExpression();
        return bound;
    }
}