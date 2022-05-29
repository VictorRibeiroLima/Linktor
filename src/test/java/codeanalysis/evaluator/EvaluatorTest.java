package codeanalysis.evaluator;

import codeanalysis.binding.Binder;
import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.symbol.VariableSymbol;
import codeanalysis.syntax.SyntaxTree;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EvaluatorTest {

    @ParameterizedTest
    @MethodSource("provideExpressions")
    void evaluate(BoundExpression expression,Map<VariableSymbol, Object> variables,Object expectedResult) throws Exception {
        Evaluator evaluator = new Evaluator(expression,variables);
        Object result = evaluator.evaluate();
        assertEquals(expectedResult,result);
    }

    static Stream<Arguments> provideExpressions() throws Exception {
        Map<VariableSymbol, Object> variables = new HashMap<>();
        return Stream.of(
                Arguments.of(getExpression("1",new HashMap<>()),new HashMap<>(),1),
                Arguments.of(getExpression("1+1",new HashMap<>()),new HashMap<>(),2),
                Arguments.of(getExpression("3-2",new HashMap<>()),new HashMap<>(),1),
                Arguments.of(getExpression("2-3",new HashMap<>()),new HashMap<>(),-1),
                Arguments.of(getExpression("-3+2",new HashMap<>()),new HashMap<>(),-1),
                Arguments.of(getExpression("-3-2",new HashMap<>()),new HashMap<>(),-5),
                Arguments.of(getExpression("2*2",new HashMap<>()),new HashMap<>(),4),
                Arguments.of(getExpression("-2*2",new HashMap<>()),new HashMap<>(),-4),
                Arguments.of(getExpression("2*-2",new HashMap<>()),new HashMap<>(),-4),
                Arguments.of(getExpression("-2*-2",new HashMap<>()),new HashMap<>(),4),
                Arguments.of(getExpression("4/2",new HashMap<>()),new HashMap<>(),2),
                Arguments.of(getExpression("2+2*3",new HashMap<>()),new HashMap<>(),8),
                Arguments.of(getExpression("2*3+2",new HashMap<>()),new HashMap<>(),8),
                Arguments.of(getExpression("2*(3+2)",new HashMap<>()),new HashMap<>(),10),
                Arguments.of(getExpression("2*-1*(6-4)",new HashMap<>()),new HashMap<>(),-4),
                Arguments.of(getExpression("-5 *4-(24/2-(5+2*3)+8)",new HashMap<>()),new HashMap<>(),-29),
                Arguments.of(getExpression("false",new HashMap<>()),new HashMap<>(),false),
                Arguments.of(getExpression("true",new HashMap<>()),new HashMap<>(),true),
                Arguments.of(getExpression("!true",new HashMap<>()),new HashMap<>(),false),
                Arguments.of(getExpression("!false",new HashMap<>()),new HashMap<>(),true),
                Arguments.of(getExpression("!!false",new HashMap<>()),new HashMap<>(),false),
                Arguments.of(getExpression("!!true",new HashMap<>()),new HashMap<>(),true),
                Arguments.of(getExpression("-10",new HashMap<>()),new HashMap<>(),-10),
                Arguments.of(getExpression("+-10",new HashMap<>()),new HashMap<>(),-10),
                Arguments.of(getExpression("-+-10",new HashMap<>()),new HashMap<>(),10),
                Arguments.of(getExpression("true && true",new HashMap<>()),new HashMap<>(),true),
                Arguments.of(getExpression("true && false",new HashMap<>()),new HashMap<>(),false),
                Arguments.of(getExpression("false && true",new HashMap<>()),new HashMap<>(),false),
                Arguments.of(getExpression("false && false",new HashMap<>()),new HashMap<>(),false),
                Arguments.of(getExpression("false || false",new HashMap<>()),new HashMap<>(),false),
                Arguments.of(getExpression("false || true",new HashMap<>()),new HashMap<>(),true),
                Arguments.of(getExpression("true || false",new HashMap<>()),new HashMap<>(),true),
                Arguments.of(getExpression("true || true",new HashMap<>()),new HashMap<>(),true),
                Arguments.of(getExpression("!true || !true",new HashMap<>()),new HashMap<>(),false),
                Arguments.of(getExpression("-5 *4-(24/2-(5+2*3)+8) == -29",new HashMap<>()),new HashMap<>(),true),
                Arguments.of(getExpression("-5 *4-(24/2-(5+2*3)+8) == 29",new HashMap<>()),new HashMap<>(),false),
                Arguments.of(getExpression("-5 *4-(24/2-(5+2*3)+8) == 29 || 1+1 ==2",new HashMap<>()),new HashMap<>(),true),
                Arguments.of(getExpression("-5 *4-(24/2-(5+2*3)+8) == 29 && 1+1 ==2",new HashMap<>()),new HashMap<>(),false),
                Arguments.of(getExpression("a = -5 *4-(24/2-(5+2*3)+8) == 29 && 1+1 ==2",variables),variables,false),
                Arguments.of(getExpression("!a",variables),variables,true),
                Arguments.of(getExpression("!!a",variables),variables,false),
                Arguments.of(getExpression("b=10",variables),variables,10),
                Arguments.of(getExpression("c=25",variables),variables,25),
                Arguments.of(getExpression("b+c",variables),variables,35)
        );
    }

    static BoundExpression getExpression(String input,Map<VariableSymbol, Object> variables ) throws Exception {
        SyntaxTree tree = SyntaxTree.parse(input);
        Binder binder = new Binder(variables);
        BoundExpression bound = binder.bindExpression(tree.getRoot());
        return bound;
    }
}