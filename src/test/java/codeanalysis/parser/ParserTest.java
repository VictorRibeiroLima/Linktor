package codeanalysis.parser;

import codeanalysis.diagnostics.text.SourceText;
import codeanalysis.syntax.AssertingList;
import codeanalysis.syntax.SyntaxFacts;
import codeanalysis.syntax.SyntaxKind;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

class ParserTest {

    @ParameterizedTest
    @MethodSource("provideBinaryOperatorPairs")
    void parseBinaryExpressionHonorsPrecedence(SyntaxKind op1, SyntaxKind op2) {
        int precedence1 = SyntaxFacts.getBinaryOperatorPrecedence(op1);
        int precedence2 = SyntaxFacts.getBinaryOperatorPrecedence(op2);
        String text1 = SyntaxFacts.getText(op1);
        String text2 = SyntaxFacts.getText(op2);

        String text = "a " + text1 + " b " + text2 + " c";

        SourceText input = SourceText.from(text);
        Parser parser = new Parser(input);
        AssertingList asserting = new AssertingList(parser.parseCompilationUnit().getMembers().get(0));

        if (precedence1 >= precedence2) {
            /*

                  +
                 / \
                *  c
               / \
               a  b
             */

            asserting.assertNode(SyntaxKind.GLOBAL_MEMBER);
            asserting.assertNode(SyntaxKind.EXPRESSION_STATEMENT);
            asserting.assertNode(SyntaxKind.BINARY_EXPRESSION);
            asserting.assertNode(SyntaxKind.BINARY_EXPRESSION);
            asserting.assertNode(SyntaxKind.NAME_EXPRESSION);
            asserting.assertToken(SyntaxKind.IDENTIFIER_TOKEN, "a");
            asserting.assertToken(op1, text1);
            asserting.assertNode(SyntaxKind.NAME_EXPRESSION);
            asserting.assertToken(SyntaxKind.IDENTIFIER_TOKEN, "b");
            asserting.assertToken(op2, text2);
            asserting.assertNode(SyntaxKind.NAME_EXPRESSION);
            asserting.assertToken(SyntaxKind.IDENTIFIER_TOKEN, "c");
        } else {
            /*

                  +
                 / \
                a   *
                   / \
                   b  c

             */
            asserting.assertNode(SyntaxKind.GLOBAL_MEMBER);
            asserting.assertNode(SyntaxKind.EXPRESSION_STATEMENT);
            asserting.assertNode(SyntaxKind.BINARY_EXPRESSION);
            asserting.assertNode(SyntaxKind.NAME_EXPRESSION);
            asserting.assertToken(SyntaxKind.IDENTIFIER_TOKEN, "a");
            asserting.assertToken(op1, text1);
            asserting.assertNode(SyntaxKind.BINARY_EXPRESSION);
            asserting.assertNode(SyntaxKind.NAME_EXPRESSION);
            asserting.assertToken(SyntaxKind.IDENTIFIER_TOKEN, "b");
            asserting.assertToken(op2, text2);
            asserting.assertNode(SyntaxKind.NAME_EXPRESSION);
            asserting.assertToken(SyntaxKind.IDENTIFIER_TOKEN, "c");
        }
    }

    @ParameterizedTest
    @MethodSource("provideBinaryOperatorPairs")
    void parseParenthesizedExpressionHonorsPrecedence(SyntaxKind op1, SyntaxKind op2) {
        String text1 = SyntaxFacts.getText(op1);
        String text2 = SyntaxFacts.getText(op2);

        String text = "(a " + text1 + " b )" + text2 + " c";

        SourceText input = SourceText.from(text);
        Parser parser = new Parser(input);
        AssertingList asserting = new AssertingList(parser.parseCompilationUnit().getMembers().get(0));
        /*

                  *
                 / \
                +  c
               / \
               a  b
        */

        asserting.assertNode(SyntaxKind.GLOBAL_MEMBER);
        asserting.assertNode(SyntaxKind.EXPRESSION_STATEMENT);
        asserting.assertNode(SyntaxKind.BINARY_EXPRESSION);
        asserting.assertNode(SyntaxKind.PARENTHESIZED_EXPRESSION);
        asserting.assertNode(SyntaxKind.OPEN_PARENTHESIS_TOKEN);
        asserting.assertNode(SyntaxKind.BINARY_EXPRESSION);
        asserting.assertNode(SyntaxKind.NAME_EXPRESSION);
        asserting.assertToken(SyntaxKind.IDENTIFIER_TOKEN, "a");
        asserting.assertToken(op1, text1);
        asserting.assertNode(SyntaxKind.NAME_EXPRESSION);
        asserting.assertToken(SyntaxKind.IDENTIFIER_TOKEN, "b");
        asserting.assertNode(SyntaxKind.CLOSE_PARENTHESIS_TOKEN);
        asserting.assertToken(op2, text2);
        asserting.assertNode(SyntaxKind.NAME_EXPRESSION);
        asserting.assertToken(SyntaxKind.IDENTIFIER_TOKEN, "c");

    }

    @ParameterizedTest
    @MethodSource("provideBinaryUnaryOperatorPairs")
    void parseBinaryUnaryExpressionHonorsPrecedence(SyntaxKind binaryExpression, SyntaxKind unaryExpression) {
        String binaryText = SyntaxFacts.getText(binaryExpression);
        String unaryText = SyntaxFacts.getText(unaryExpression);

        String text = "a " + binaryText + unaryText + " b";
        SourceText input = SourceText.from(text);
        Parser parser = new Parser(input);
        AssertingList asserting = new AssertingList(parser.parseCompilationUnit().getMembers().get(0));
        /*

                  +
                 / \
                a   !
                     \
                      b
             */

        asserting.assertNode(SyntaxKind.GLOBAL_MEMBER);
        asserting.assertNode(SyntaxKind.EXPRESSION_STATEMENT);
        asserting.assertNode(SyntaxKind.BINARY_EXPRESSION);
        asserting.assertNode(SyntaxKind.NAME_EXPRESSION);
        asserting.assertToken(SyntaxKind.IDENTIFIER_TOKEN, "a");
        asserting.assertToken(binaryExpression, binaryText);
        asserting.assertNode(SyntaxKind.UNARY_EXPRESSION);
        asserting.assertToken(unaryExpression, unaryText);
        asserting.assertNode(SyntaxKind.NAME_EXPRESSION);
        asserting.assertToken(SyntaxKind.IDENTIFIER_TOKEN, "b");

    }

    @ParameterizedTest
    @MethodSource("provideBinaryUnaryOperatorPairs")
    void parseUnaryBinaryExpressionHonorsPrecedence(SyntaxKind binaryExpression, SyntaxKind unaryExpression) {
        String binaryText = SyntaxFacts.getText(binaryExpression);
        String unaryText = SyntaxFacts.getText(unaryExpression);

        String text = unaryText + "a " + binaryText + " b";
        SourceText input = SourceText.from(text);
        Parser parser = new Parser(input);
        AssertingList asserting = new AssertingList(parser.parseCompilationUnit().getMembers().get(0));
        /*

                  +
                 / \
                !   b
               /
              a

             */

        asserting.assertNode(SyntaxKind.GLOBAL_MEMBER);
        asserting.assertNode(SyntaxKind.EXPRESSION_STATEMENT);
        asserting.assertNode(SyntaxKind.BINARY_EXPRESSION);
        asserting.assertNode(SyntaxKind.UNARY_EXPRESSION);
        asserting.assertToken(unaryExpression, unaryText);
        asserting.assertNode(SyntaxKind.NAME_EXPRESSION);
        asserting.assertToken(SyntaxKind.IDENTIFIER_TOKEN, "a");
        asserting.assertToken(binaryExpression, binaryText);
        asserting.assertNode(SyntaxKind.NAME_EXPRESSION);
        asserting.assertToken(SyntaxKind.IDENTIFIER_TOKEN, "b");
    }

    @ParameterizedTest
    @MethodSource("provideBinaryUnaryOperatorPairs")
    void parseAssignmentExpressionHonorsPrecedence(SyntaxKind binaryExpression, SyntaxKind unaryExpression) {
        String binaryText = SyntaxFacts.getText(binaryExpression);
        String unaryText = SyntaxFacts.getText(unaryExpression);

        String text = "a=" + unaryText + "b " + binaryText + " c";

        /*
                   =
                  / \
                 a   +
                     / \
                    !   c
                   /
                  b

        */

        SourceText input = SourceText.from(text);
        Parser parser = new Parser(input);
        AssertingList asserting = new AssertingList(parser.parseCompilationUnit().getMembers().get(0));

        asserting.assertNode(SyntaxKind.GLOBAL_MEMBER);
        asserting.assertNode(SyntaxKind.EXPRESSION_STATEMENT);
        asserting.assertNode(SyntaxKind.ASSIGNMENT_EXPRESSION);
        asserting.assertToken(SyntaxKind.IDENTIFIER_TOKEN, "a");
        asserting.assertToken(SyntaxKind.EQUAL_TOKEN, "=");
        asserting.assertNode(SyntaxKind.BINARY_EXPRESSION);
        asserting.assertNode(SyntaxKind.UNARY_EXPRESSION);
        asserting.assertToken(unaryExpression, unaryText);
        asserting.assertNode(SyntaxKind.NAME_EXPRESSION);
        asserting.assertToken(SyntaxKind.IDENTIFIER_TOKEN, "b");
        asserting.assertToken(binaryExpression, binaryText);
        asserting.assertNode(SyntaxKind.NAME_EXPRESSION);
        asserting.assertToken(SyntaxKind.IDENTIFIER_TOKEN, "c");

    }


    @ParameterizedTest
    @MethodSource("provideAssignableKinds")
    void parseAssignmentExpression(SyntaxKind expectedKind, String expectedText) {
        String text = "a=" + expectedText;
        SourceText input = SourceText.from(text);
        Parser parser = new Parser(input);
        AssertingList asserting = new AssertingList(parser.parseCompilationUnit().getMembers().get(0));
        asserting.assertNode(SyntaxKind.GLOBAL_MEMBER);
        asserting.assertNode(SyntaxKind.EXPRESSION_STATEMENT);
        asserting.assertNode(SyntaxKind.ASSIGNMENT_EXPRESSION);
        asserting.assertToken(SyntaxKind.IDENTIFIER_TOKEN, "a");
        asserting.assertNode(SyntaxKind.EQUAL_TOKEN);
        asserting.assertNode(SyntaxKind.LITERAL_EXPRESSION);
        asserting.assertToken(expectedKind, expectedText);
    }

    @Test
    void parseBadParenthesizedExpression() {
        String text = "(1";
        SourceText input = SourceText.from(text);
        Parser parser = new Parser(input);
        AssertingList asserting = new AssertingList(parser.parseCompilationUnit().getMembers().get(0));
        asserting.assertNode(SyntaxKind.GLOBAL_MEMBER);
        asserting.assertNode(SyntaxKind.EXPRESSION_STATEMENT);
        asserting.assertNode(SyntaxKind.PARENTHESIZED_EXPRESSION);
        asserting.assertNode(SyntaxKind.OPEN_PARENTHESIS_TOKEN);
        asserting.assertNode(SyntaxKind.LITERAL_EXPRESSION);
        asserting.assertToken(SyntaxKind.NUMBER_TOKEN, "1");
        asserting.assertNode(SyntaxKind.CLOSE_PARENTHESIS_TOKEN);
    }

    @ParameterizedTest
    @MethodSource("provideUnaryOperatorPairs")
    void parseUnaryUnaryExpression(SyntaxKind u1, SyntaxKind u2) {
        String u1Text = SyntaxFacts.getText(u1);
        String u2Text = SyntaxFacts.getText(u2);

        String text = u1Text + u2Text + "a";
        SourceText input = SourceText.from(text);
        Parser parser = new Parser(input);
        AssertingList asserting = new AssertingList(parser.parseCompilationUnit().getMembers().get(0));
        /*

                  !
                 /
                !
               /
              a

             */

        asserting.assertNode(SyntaxKind.GLOBAL_MEMBER);
        asserting.assertNode(SyntaxKind.EXPRESSION_STATEMENT);
        asserting.assertNode(SyntaxKind.UNARY_EXPRESSION);
        asserting.assertToken(u1, u1Text);
        asserting.assertNode(SyntaxKind.UNARY_EXPRESSION);
        asserting.assertToken(u2, u2Text);
        asserting.assertNode(SyntaxKind.NAME_EXPRESSION);
        asserting.assertToken(SyntaxKind.IDENTIFIER_TOKEN, "a");
    }


    static Stream<Arguments> provideBinaryOperatorPairs() {
        List<SyntaxKind> bos1 = SyntaxFacts.getBinaryOperatorKinds();
        List<SyntaxKind> bos2 = SyntaxFacts.getBinaryOperatorKinds();
        List<Arguments> args = new ArrayList<>();
        for (SyntaxKind bo1 : bos1) {
            for (SyntaxKind bo2 : bos2) {
                args.add(Arguments.of(bo1, bo2));
            }
        }
        return args.stream();
    }

    static Stream<Arguments> provideBinaryUnaryOperatorPairs() {
        List<SyntaxKind> bos1 = SyntaxFacts.getBinaryOperatorKinds();
        List<SyntaxKind> bos2 = SyntaxFacts.getUnaryOperatorKinds();
        List<Arguments> args = new ArrayList<>();
        for (SyntaxKind bo1 : bos1) {
            for (SyntaxKind bo2 : bos2) {
                args.add(Arguments.of(bo1, bo2));
            }
        }
        return args.stream();
    }

    static Stream<Arguments> provideUnaryOperatorPairs() {
        List<SyntaxKind> bos1 = SyntaxFacts.getUnaryOperatorKinds();
        List<SyntaxKind> bos2 = SyntaxFacts.getUnaryOperatorKinds();
        List<Arguments> args = new ArrayList<>();
        for (SyntaxKind bo1 : bos1) {
            for (SyntaxKind bo2 : bos2) {
                args.add(Arguments.of(bo1, bo2));
            }
        }
        return args.stream();
    }

    static Stream<Arguments> provideAssignableKinds() {
        return Stream.of(
                Arguments.of(SyntaxKind.NUMBER_TOKEN, "1"),
                Arguments.of(SyntaxKind.NUMBER_TOKEN, "123"),
                Arguments.of(SyntaxKind.FALSE_KEYWORD, "false"),
                Arguments.of(SyntaxKind.TRUE_KEYWORD, "true")
        );
    }
}