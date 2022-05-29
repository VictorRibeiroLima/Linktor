package codeanalysis.parser;

import codeanalysis.syntax.AssertingList;
import codeanalysis.syntax.SyntaxFacts;
import codeanalysis.syntax.SyntaxKind;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

class ParserTest {

    @ParameterizedTest
    @MethodSource("provideBinaryOperatorPairs")
    void parseBinaryExpressionHonorsPrecedence(SyntaxKind op1,SyntaxKind op2) {
        int precedence1 = SyntaxFacts.getBinaryOperatorPrecedence(op1);
        int precedence2 = SyntaxFacts.getBinaryOperatorPrecedence(op2);
        String text1 = SyntaxFacts.getText(op1);
        String text2 = SyntaxFacts.getText(op2);

        String text = "a "+text1+" b "+text2+" c";

        Parser parser = new Parser(text);
        AssertingList asserting = new AssertingList(parser.parse().getRoot());

        if(precedence1 >= precedence2){
            /*

                  +
                 / \
                *  c
               / \
               a  b
             */

            asserting.assertNode(SyntaxKind.BINARY_EXPRESSION);
                asserting.assertNode(SyntaxKind.BINARY_EXPRESSION);
                    asserting.assertNode(SyntaxKind.NAME_EXPRESSION);
                        asserting.assertToken(SyntaxKind.IDENTIFIER_TOKEN,"a");
                    asserting.assertToken(op1,text1);
                    asserting.assertNode(SyntaxKind.NAME_EXPRESSION);
                        asserting.assertToken(SyntaxKind.IDENTIFIER_TOKEN,"b");
                asserting.assertToken(op2,text2);
                asserting.assertNode(SyntaxKind.NAME_EXPRESSION);
                    asserting.assertToken(SyntaxKind.IDENTIFIER_TOKEN,"c");
        }else {
            /*

                  +
                 / \
                a   *
                   / \
                   b  c

             */
            asserting.assertNode(SyntaxKind.BINARY_EXPRESSION);
                asserting.assertNode(SyntaxKind.NAME_EXPRESSION);
                    asserting.assertToken(SyntaxKind.IDENTIFIER_TOKEN,"a");
                asserting.assertToken(op1,text1);
            asserting.assertNode(SyntaxKind.BINARY_EXPRESSION);
                asserting.assertNode(SyntaxKind.NAME_EXPRESSION);
                    asserting.assertToken(SyntaxKind.IDENTIFIER_TOKEN,"b");
                asserting.assertToken(op2,text2);
                asserting.assertNode(SyntaxKind.NAME_EXPRESSION);
                    asserting.assertToken(SyntaxKind.IDENTIFIER_TOKEN,"c");
        }
    }

    @ParameterizedTest
    @MethodSource("provideBinaryUnaryOperatorPairs")
    void parseBinaryUnaryExpressionHonorsPrecedence(SyntaxKind binaryExpression,SyntaxKind unaryExpression){
        String binaryText = SyntaxFacts.getText(binaryExpression);
        String unaryText = SyntaxFacts.getText(unaryExpression);

        String text = "a "+binaryText+ unaryText+" b";
        Parser parser = new Parser(text);
        AssertingList asserting = new AssertingList(parser.parse().getRoot());
        /*

                  +
                 / \
                a   !
                     \
                      b
             */

        asserting.assertNode(SyntaxKind.BINARY_EXPRESSION);
            asserting.assertNode(SyntaxKind.NAME_EXPRESSION);
                asserting.assertToken(SyntaxKind.IDENTIFIER_TOKEN,"a");
            asserting.assertToken(binaryExpression,binaryText);
            asserting.assertNode(SyntaxKind.UNARY_EXPRESSION);
                asserting.assertToken(unaryExpression,unaryText);
                asserting.assertNode(SyntaxKind.NAME_EXPRESSION);
                    asserting.assertToken(SyntaxKind.IDENTIFIER_TOKEN,"b");

    }

    @ParameterizedTest
    @MethodSource("provideBinaryUnaryOperatorPairs")
    void parseUnaryBinaryExpressionHonorsPrecedence(SyntaxKind binaryExpression,SyntaxKind unaryExpression){
        String binaryText = SyntaxFacts.getText(binaryExpression);
        String unaryText = SyntaxFacts.getText(unaryExpression);

        String text = unaryText+"a "+binaryText+" b";
        Parser parser = new Parser(text);
        AssertingList asserting = new AssertingList(parser.parse().getRoot());
        /*

                  +
                 / \
                !   b
               /
              a

             */

        asserting.assertNode(SyntaxKind.BINARY_EXPRESSION);
            asserting.assertNode(SyntaxKind.UNARY_EXPRESSION);
                asserting.assertToken(unaryExpression,unaryText);
                asserting.assertNode(SyntaxKind.NAME_EXPRESSION);
                    asserting.assertToken(SyntaxKind.IDENTIFIER_TOKEN,"a");
            asserting.assertToken(binaryExpression,binaryText);
            asserting.assertNode(SyntaxKind.NAME_EXPRESSION);
                asserting.assertToken(SyntaxKind.IDENTIFIER_TOKEN,"b");
    }

    @ParameterizedTest
    @MethodSource("provideUnaryOperatorPairs")
    void parseUnaryUnaryExpression(SyntaxKind u1,SyntaxKind u2){
        String u1Text = SyntaxFacts.getText(u1);
        String u2Text = SyntaxFacts.getText(u2);

        String text = u1Text+u2Text+"a";
        Parser parser = new Parser(text);
        AssertingList asserting = new AssertingList(parser.parse().getRoot());
        /*

                  !
                 /
                !
               /
              a

             */

        asserting.assertNode(SyntaxKind.UNARY_EXPRESSION);
        asserting.assertToken(u1,u1Text);
            asserting.assertNode(SyntaxKind.UNARY_EXPRESSION);
            asserting.assertToken(u2,u2Text);
                asserting.assertNode(SyntaxKind.NAME_EXPRESSION);
                    asserting.assertToken(SyntaxKind.IDENTIFIER_TOKEN,"a");
    }

    static Stream<Arguments> provideBinaryOperatorPairs(){
        List<SyntaxKind> bos1 = SyntaxFacts.getBinaryOperatorKinds();
        List<SyntaxKind> bos2 = SyntaxFacts.getBinaryOperatorKinds();
        List<Arguments> args = new ArrayList<>();
        for (SyntaxKind bo1:bos1) {
            for (SyntaxKind bo2:bos2) {
                args.add(Arguments.of(bo1,bo2));
            }
        }
        return args.stream();
    }

    static Stream<Arguments> provideBinaryUnaryOperatorPairs(){
        List<SyntaxKind> bos1 = SyntaxFacts.getBinaryOperatorKinds();
        List<SyntaxKind> bos2 = SyntaxFacts.getUnaryOperatorKinds();
        List<Arguments> args = new ArrayList<>();
        for (SyntaxKind bo1:bos1) {
            for (SyntaxKind bo2:bos2) {
                args.add(Arguments.of(bo1,bo2));
            }
        }
        return args.stream();
    }

    static Stream<Arguments> provideUnaryOperatorPairs(){
        List<SyntaxKind> bos1 = SyntaxFacts.getUnaryOperatorKinds();
        List<SyntaxKind> bos2 = SyntaxFacts.getUnaryOperatorKinds();
        List<Arguments> args = new ArrayList<>();
        for (SyntaxKind bo1:bos1) {
            for (SyntaxKind bo2:bos2) {
                args.add(Arguments.of(bo1,bo2));
            }
        }
        return args.stream();
    }
}