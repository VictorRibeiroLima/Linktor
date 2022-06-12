package codeanalysis.evaluator;

import codeanalysis.binding.scopes.BoundGlobalScope;
import codeanalysis.diagnostics.Diagnostic;
import codeanalysis.source.TextSpan;
import codeanalysis.symbol.variable.VariableSymbol;
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
    void evaluate(String expression, Object expectedResult) throws Exception {
        AnnotatedText input = AnnotatedText.parse(expression);
        SyntaxTree tree = SyntaxTree.parse(input.getText());
        Compilation compilation = new Compilation(tree);
        EvaluationResult result = compilation.evaluate(new HashMap<>());
        assertEquals(expectedResult, result.result());
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
        assertEquals(expectedSpan, diagnostic.location().span());

    }

    static Stream<Arguments> provideExpressions() throws Exception {

        return Stream.of(
                Arguments.of("1", 1),
                Arguments.of("1+1", 2),
                Arguments.of("3-2", 1),
                Arguments.of("2-3", -1),
                Arguments.of("-3+2", -1),
                Arguments.of("-3-2", -5),
                Arguments.of("2*2", 4),
                Arguments.of("-2*2", -4),
                Arguments.of("2*-2", -4),
                Arguments.of("-2*-2", 4),
                Arguments.of("4/2", 2),
                Arguments.of("2+2*3", 8),
                Arguments.of("2*3+2", 8),
                Arguments.of("2*(3+2)", 10),
                Arguments.of("2*-1*(6-4)", -4),
                Arguments.of("-5 *4-(24/2-(5+2*3)+8)", -29),
                Arguments.of("false", false),
                Arguments.of("true", true),
                Arguments.of("!true", false),
                Arguments.of("!false", true),
                Arguments.of("!!false", false),
                Arguments.of("!!true", true),
                Arguments.of("-10", -10),
                Arguments.of("+-10", -10),
                Arguments.of("-+-10", 10),
                Arguments.of("true && true", true),
                Arguments.of("true && false", false),
                Arguments.of("false && true", false),
                Arguments.of("false && false", false),
                Arguments.of("false || false", false),
                Arguments.of("false || true", true),
                Arguments.of("true || false", true),
                Arguments.of("true || true", true),
                Arguments.of("!true || !true", false),
                Arguments.of("-5 *4-(24/2-(5+2*3)+8) == -29", true),
                Arguments.of("-5 *4-(24/2-(5+2*3)+8) == 29", false),
                Arguments.of("-5 *4-(24/2-(5+2*3)+8) == 29 || 1+1 ==2", true),
                Arguments.of("-5 *4-(24/2-(5+2*3)+8) == 29 && 1+1 ==2", false),
                Arguments.of("var a = -5 *4-(24/2-(5+2*3)+8) == 29 && 1+1 ==2", false),
                Arguments.of("let b=10", 10),
                Arguments.of("let c=25", 25),
                Arguments.of("~false", -1),
                Arguments.of("~true", -2),
                Arguments.of("false|true", true),
                Arguments.of("false&true", false),
                Arguments.of("false^true", true),
                Arguments.of("~123", -124),
                Arguments.of("3 | 4", 7),
                Arguments.of("3 & 4", 0),
                Arguments.of("5 ^ 4", 1)
        );
    }

    static Stream<Arguments> provideDiagnostics() {
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
                ), Arguments.of("""
                                {
                                    let a = [']10;
                                }
                                """,
                        "ERROR: Unterminated string literal."
                ), Arguments.of("""
                                {
                                    let a = ["]10;
                                }
                                """,
                        "ERROR: Unterminated string literal."
                ), Arguments.of("""
                                {
                                    var a = 10;
                                    a = ["20"]
                                }
                                """,
                        "ERROR: Cannot convert 'string' into 'int'."
                ), Arguments.of("""
                                {
                                    var a:int = ["10"];
                                }
                                """,
                        "ERROR: Cannot convert 'string' into 'int'."
                ), Arguments.of("""
                                {
                                   for(var i:string = [0];i<10;i=i+1){
                                    
                                   }
                                }
                                """,
                        "ERROR: Cannot convert 'int' into 'string'."
                )
        );
    }


}