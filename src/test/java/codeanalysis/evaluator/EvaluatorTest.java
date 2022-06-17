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
    @MethodSource("provideDiagnostics")
    void diagnostics(String text, String expectedDiagnostic) throws Exception {
        AnnotatedText input = AnnotatedText.parse(text);
        SyntaxTree tree = SyntaxTree.parse(input.getText());
        Compilation compilation = Compilation.createScript(null, tree);
        EvaluationResult result = compilation.evaluate(new HashMap<>());
        assertTrue(result.diagnostics().size() > 0);
        Diagnostic diagnostic = result.diagnostics().get(0);
        TextSpan expectedSpan = input.getSpans().get(0);

        assertEquals(expectedDiagnostic, diagnostic.message());
        assertEquals(expectedSpan, diagnostic.location().span());

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