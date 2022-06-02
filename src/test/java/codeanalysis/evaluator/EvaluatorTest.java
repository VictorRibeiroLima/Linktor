package codeanalysis.evaluator;

import codeanalysis.binding.Binder;
import codeanalysis.binding.scopes.BoundGlobalScope;
import codeanalysis.binding.statement.BoundStatement;
import codeanalysis.diagnostics.Diagnostic;
import codeanalysis.diagnostics.text.TextSpan;
import codeanalysis.symbol.VariableSymbol;
import codeanalysis.syntax.SyntaxTree;
import compilation.Compilation;
import compilation.EvaluationResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EvaluatorTest {

    static BoundGlobalScope globalScope = null;

    static Map<VariableSymbol, Object> variables;

    @BeforeAll
    static void setUp() {
        variables = new HashMap<>();
    }

    @ParameterizedTest
    @MethodSource("provideExpressions")
    void evaluate(BoundStatement expression, Object expectedResult) throws Exception {
        Evaluator evaluator = new Evaluator(expression, variables);
        Object result = evaluator.evaluate();
        assertEquals(expectedResult, result);
    }

    @ParameterizedTest
    @MethodSource("provideDiagnostics")
    void diagnostics(String text, String expectedDiagnostic) throws Exception {
        AnnotatedText input = AnnotatedText.parse(text);
        SyntaxTree tree = SyntaxTree.parse(input.getText());
        Compilation compilation = new Compilation(tree);
        EvaluationResult result = compilation.evaluate(new HashMap<>());
        assertTrue(result.diagnostics().size() > 0);
        Diagnostic diagnostic = result.diagnostics().get(0);
        TextSpan expectedSpan = input.getSpans().get(0);

        assertEquals(expectedDiagnostic, diagnostic.message());
        assertEquals(expectedSpan, diagnostic.span());

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
                Arguments.of(getExpression("var a = -5 *4-(24/2-(5+2*3)+8) == 29 && 1+1 ==2"), false),
                Arguments.of(getExpression("!a"), true),
                Arguments.of(getExpression("!!a"), false),
                Arguments.of(getExpression("let b=10"), 10),
                Arguments.of(getExpression("let c=25"), 25),
                Arguments.of(getExpression("b+c"), 35),
                Arguments.of(getExpression("c>b"), true),
                Arguments.of(getExpression("b<=c"), true),
                Arguments.of(getExpression("c>100"), false),
                Arguments.of(getExpression("b<=1"), false),
                Arguments.of(getExpression("~false"), -1),
                Arguments.of(getExpression("~true"), -2),
                Arguments.of(getExpression("false|true"), true),
                Arguments.of(getExpression("false&true"), false),
                Arguments.of(getExpression("false^true"), true),
                Arguments.of(getExpression("~123"), -124),
                Arguments.of(getExpression("3 | 4"), 7),
                Arguments.of(getExpression("3 & 4"), 0),
                Arguments.of(getExpression("5 ^ 4"), 1)
        );
    }

    static Stream<Arguments> provideDiagnostics() throws Exception {
        return Stream.of(
                Arguments.of("""
                                {
                                    var a = 10;
                                    var [a] = 100;
                                }
                                """,
                        "ERROR: variable 'a' is already declared."
                ),
                Arguments.of("""
                                {
                                    let a = 10;
                                    a [=] 100;
                                }
                                """,
                        "ERROR: Variable 'a' is read only and cannot be assigned."
                )
        );
    }

    static BoundStatement getExpression(String input) throws Exception {
        SyntaxTree tree = SyntaxTree.parse(input);
        BoundGlobalScope localGlobalScope = Binder.boundGlobalScope(tree.getRoot(), globalScope);
        globalScope = localGlobalScope;
        BoundStatement bound = localGlobalScope.getStatement();
        return bound;
    }


}