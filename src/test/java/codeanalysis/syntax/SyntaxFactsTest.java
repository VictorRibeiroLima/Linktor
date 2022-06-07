package codeanalysis.syntax;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SyntaxFactsTest {
    @ParameterizedTest
    @MethodSource("provideKind")
    void getText(SyntaxKind kind) {
        String text = SyntaxFacts.getText(kind);
        if (text == null)
            return;
        List<SyntaxToken> tokens = SyntaxTree.parseTokens(text);
        assertEquals(1, tokens.size());
        assertEquals(kind, tokens.get(0).getKind());
    }

    @ParameterizedTest
    @MethodSource("provideUnaryOperators")
    void unaryOperatorPrecedence(SyntaxKind operator, int expectedPrecedence) {
        int precedence = SyntaxFacts.getUnaryOperatorPrecedence(operator);
        assertEquals(expectedPrecedence, precedence);
    }

    @ParameterizedTest
    @MethodSource("provideBinaryOperators")
    void binaryOperatorPrecedence(SyntaxKind operator, int expectedPrecedence) {
        int precedence = SyntaxFacts.getBinaryOperatorPrecedence(operator);
        assertEquals(expectedPrecedence, precedence);
    }

    @Test
    void getUnaryOperatorKinds() {
        List<SyntaxKind> unaryOperators = SyntaxFacts.getUnaryOperatorKinds();
        assertEquals(4, unaryOperators.size());
    }

    @Test
    void getBinaryOperatorKinds() {
        List<SyntaxKind> binaryOperatorKinds = SyntaxFacts.getBinaryOperatorKinds();
        assertEquals(16, binaryOperatorKinds.size());
    }

    private static Stream<Arguments> provideKind() {
        SyntaxKind[] kinds = SyntaxKind.values();
        List<Arguments> args = new ArrayList<>();
        for (SyntaxKind kind : kinds) {
            args.add(Arguments.of(kind));
        }
        return args.stream();
    }

    private static Stream<Arguments> provideUnaryOperators() {
        return Stream.of(
                Arguments.of(SyntaxKind.PLUS_TOKEN, 6),
                Arguments.of(SyntaxKind.MINUS_TOKEN, 6),
                Arguments.of(SyntaxKind.EXCLAMATION_TOKEN, 6),
                Arguments.of(SyntaxKind.BAD_TOKEN, 0)
        );
    }

    private static Stream<Arguments> provideBinaryOperators() {
        return Stream.of(
                Arguments.of(SyntaxKind.STAR_TOKEN, 5),
                Arguments.of(SyntaxKind.SLASH_TOKEN, 5),
                Arguments.of(SyntaxKind.PLUS_TOKEN, 4),
                Arguments.of(SyntaxKind.MINUS_TOKEN, 4),
                Arguments.of(SyntaxKind.EXCLAMATION_EQUAL_TOKEN, 3),
                Arguments.of(SyntaxKind.EQUAL_EQUAL_TOKEN, 3),
                Arguments.of(SyntaxKind.AMPERSAND_AMPERSAND_TOKEN, 2),
                Arguments.of(SyntaxKind.PIPE_PIPE_TOKEN, 1),
                Arguments.of(SyntaxKind.BAD_TOKEN, 0)
        );
    }
}