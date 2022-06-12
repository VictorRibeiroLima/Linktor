package codeanalysis.lexer;

import codeanalysis.source.SourceText;
import codeanalysis.syntax.SyntaxFacts;
import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxToken;
import codeanalysis.syntax.SyntaxTree;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LexerTest {

    @Test
    void testAllTokens() {
        List<SyntaxKind> tokenKinds = Arrays.stream(SyntaxKind.values())
                .filter(syntaxKind -> syntaxKind.toString().endsWith("KEYWORD")
                        || syntaxKind.toString().endsWith("TOKEN")
                        && syntaxKind != SyntaxKind.BAD_TOKEN
                        && syntaxKind != SyntaxKind.END_OF_FILE_TOKEN
                )
                .toList();

        List<SyntaxKind> testedTokens = Stream.concat(getTokens().map(arg -> (SyntaxKind) arg.get()[0])
                , getSeparator().map(arg -> (SyntaxKind) arg.get()[0])).toList();

        List<SyntaxKind> untestedTokens = new ArrayList<>(tokenKinds);
        untestedTokens.removeAll(testedTokens);

        assertTrue(untestedTokens.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("provideTokens")
    void lexToken(SyntaxKind kind, String text) {
        SourceText input = SourceText.from(text);
        var tree = SyntaxTree.parse(input);
        Lexer lexer = new Lexer(tree);
        SyntaxToken token = lexer.lex();
        assertEquals(kind, token.getKind());
        assertEquals(text, token.getText());
    }

    @ParameterizedTest
    @MethodSource("provideTokenPairs")
    void lexTokenPairs(SyntaxKind k1, String t1, SyntaxKind k2, String t2) {
        String text = t1 + t2;
        List<SyntaxToken> tokens = SyntaxTree.parseTokens(text);
        assertEquals(2, tokens.size());
        assertEquals(tokens.get(0).getKind(), k1);
        assertEquals(tokens.get(0).getText(), t1);
        assertEquals(tokens.get(1).getKind(), k2);
        assertEquals(tokens.get(1).getText(), t2);
    }

    @ParameterizedTest
    @MethodSource("provideTokenPairsWithSeparator")
    void lexTokenWithSeparators(SyntaxKind k1, String t1,
                                SyntaxKind separatorKind, String separatorText,
                                SyntaxKind k2, String t2) {
        String text = t1 + separatorText + t2;
        List<SyntaxToken> tokens = SyntaxTree.parseTokens(text);
        assertEquals(tokens.size(), 3);
        assertEquals(tokens.get(0).getKind(), k1);
        assertEquals(tokens.get(0).getText(), t1);
        assertEquals(tokens.get(1).getKind(), separatorKind);
        assertEquals(tokens.get(1).getText(), separatorText);
        assertEquals(tokens.get(2).getKind(), k2);
        assertEquals(tokens.get(2).getText(), t2);
    }

    private static Stream<Arguments> provideTokens() {
        return Stream.concat(getTokens(), getSeparator());
    }

    private static Stream<Arguments> provideTokenPairs() {
        List<Arguments> arguments = getTokens().toList();
        List<Arguments> pairs = new ArrayList<>();
        for (Arguments a1 : arguments) {
            SyntaxKind k1 = (SyntaxKind) a1.get()[0];
            String t1 = (String) a1.get()[1];
            for (Arguments a2 : arguments) {
                SyntaxKind k2 = (SyntaxKind) a2.get()[0];
                String t2 = (String) a2.get()[1];
                if (!requiresSeparator(k1, k2))
                    pairs.add(Arguments.of(k1, t1, k2, t2));
            }
        }
        return pairs.stream();
    }

    private static Stream<Arguments> provideTokenPairsWithSeparator() {
        List<Arguments> arguments = getTokens().toList();
        List<Arguments> pairs = new ArrayList<>();
        for (Arguments a1 : arguments) {
            SyntaxKind k1 = (SyntaxKind) a1.get()[0];
            String t1 = (String) a1.get()[1];
            for (Arguments a2 : arguments) {
                SyntaxKind k2 = (SyntaxKind) a2.get()[0];
                String t2 = (String) a2.get()[1];
                if (requiresSeparator(k1, k2))
                    for (Arguments sa : getSeparator().toList()) {
                        SyntaxKind sk = (SyntaxKind) sa.get()[0];
                        String st = (String) sa.get()[1];
                        pairs.add(Arguments.of(k1, t1, sk, st, k2, t2));
                    }
            }
        }
        return pairs.stream();
    }

    private static Stream<Arguments> getTokens() {
        Stream<Arguments> fixedValues = Arrays.stream(SyntaxKind.values())
                .filter(syntaxKind -> SyntaxFacts.getText(syntaxKind) != null)
                .map(syntaxKind -> Arguments.of(syntaxKind, SyntaxFacts.getText(syntaxKind)));
        Stream<Arguments> dynamicValues = Stream.of(
                Arguments.of(SyntaxKind.NUMBER_TOKEN, "1"),
                Arguments.of(SyntaxKind.NUMBER_TOKEN, "123"),
                Arguments.of(SyntaxKind.IDENTIFIER_TOKEN, "a"),
                Arguments.of(SyntaxKind.IDENTIFIER_TOKEN, "abc"),
                Arguments.of(SyntaxKind.STRING_TOKEN, "'abc'"),
                Arguments.of(SyntaxKind.STRING_TOKEN, "\"abc\""),
                Arguments.of(SyntaxKind.STRING_TOKEN, "'\"abc\"'"),
                Arguments.of(SyntaxKind.STRING_TOKEN, "\"'abc'\"")
        );

        return Stream.concat(fixedValues, dynamicValues);
    }

    private static Stream<Arguments> getSeparator() {
        return Stream.of(
                Arguments.of(SyntaxKind.WHITESPACE_TOKEN, " "),
                Arguments.of(SyntaxKind.WHITESPACE_TOKEN, "  "),
                Arguments.of(SyntaxKind.WHITESPACE_TOKEN, "\r"),
                Arguments.of(SyntaxKind.WHITESPACE_TOKEN, "\n"),
                Arguments.of(SyntaxKind.WHITESPACE_TOKEN, "\n\r")
        );
    }

    private static boolean requiresSeparator(SyntaxKind k1, SyntaxKind k2) {
        if (k1.toString().endsWith("KEYWORD") && k2.toString().endsWith("KEYWORD"))
            return true;
        if (k1.toString().endsWith("KEYWORD") && k2 == SyntaxKind.IDENTIFIER_TOKEN)
            return true;
        if (k1 == SyntaxKind.IDENTIFIER_TOKEN && k2.toString().endsWith("KEYWORD"))
            return true;
        if (k1 == SyntaxKind.IDENTIFIER_TOKEN && k2 == SyntaxKind.IDENTIFIER_TOKEN)
            return true;
        if (k1 == SyntaxKind.NUMBER_TOKEN && k2 == SyntaxKind.NUMBER_TOKEN)
            return true;
        if (k1 == SyntaxKind.EXCLAMATION_TOKEN && k2 == SyntaxKind.EQUAL_TOKEN)
            return true;
        if (k1 == SyntaxKind.EQUAL_TOKEN && k2 == SyntaxKind.EQUAL_TOKEN)
            return true;
        if (k1 == SyntaxKind.EQUAL_TOKEN && k2 == SyntaxKind.EQUAL_EQUAL_TOKEN)
            return true;
        if (k1 == SyntaxKind.EXCLAMATION_TOKEN && k2 == SyntaxKind.EXCLAMATION_TOKEN)
            return true;
        if (k1 == SyntaxKind.GREATER_TOKEN && k2 == SyntaxKind.EQUAL_TOKEN)
            return true;
        if (k1 == SyntaxKind.GREATER_TOKEN && k2 == SyntaxKind.EQUAL_EQUAL_TOKEN)
            return true;
        if (k1 == SyntaxKind.LESS_TOKEN && k2 == SyntaxKind.EQUAL_TOKEN)
            return true;
        if (k1 == SyntaxKind.LESS_TOKEN && k2 == SyntaxKind.EQUAL_EQUAL_TOKEN)
            return true;
        if (k1 == SyntaxKind.AMPERSAND_TOKEN && k2 == SyntaxKind.AMPERSAND_AMPERSAND_TOKEN)
            return true;
        if (k1 == SyntaxKind.AMPERSAND_TOKEN && k2 == SyntaxKind.AMPERSAND_TOKEN)
            return true;
        if (k1 == SyntaxKind.PIPE_TOKEN && k2 == SyntaxKind.PIPE_TOKEN)
            return true;
        if (k1 == SyntaxKind.PIPE_TOKEN && k2 == SyntaxKind.PIPE_PIPE_TOKEN)
            return true;
        if (k1 == SyntaxKind.MINUS_TOKEN && k2 == SyntaxKind.MINUS_MINUS_TOKEN)
            return true;
        if (k1 == SyntaxKind.MINUS_TOKEN && k2 == SyntaxKind.MINUS_TOKEN)
            return true;
        if (k1 == SyntaxKind.PLUS_TOKEN && k2 == SyntaxKind.PLUS_TOKEN)
            return true;
        if (k1 == SyntaxKind.PLUS_TOKEN && k2 == SyntaxKind.PLUS_PLUS_TOKEN)
            return true;
        if (k1 == SyntaxKind.PLUS_TOKEN && k2 == SyntaxKind.EQUAL_TOKEN)
            return true;
        if (k1 == SyntaxKind.PLUS_TOKEN && k2 == SyntaxKind.EQUAL_EQUAL_TOKEN)
            return true;
        if (k1 == SyntaxKind.PLUS_TOKEN && k2 == SyntaxKind.PLUS_EQUALS_TOKEN)
            return true;
        if (k1 == SyntaxKind.MINUS_TOKEN && k2 == SyntaxKind.EQUAL_TOKEN)
            return true;
        if (k1 == SyntaxKind.MINUS_TOKEN && k2 == SyntaxKind.EQUAL_EQUAL_TOKEN)
            return true;
        if (k1 == SyntaxKind.MINUS_TOKEN && k2 == SyntaxKind.MINUS_EQUALS_TOKEN)
            return true;
        if (k1 == SyntaxKind.STAR_TOKEN && k2 == SyntaxKind.EQUAL_TOKEN)
            return true;
        if (k1 == SyntaxKind.STAR_TOKEN && k2 == SyntaxKind.EQUAL_EQUAL_TOKEN)
            return true;
        if (k1 == SyntaxKind.STAR_TOKEN && k2 == SyntaxKind.STAR_EQUALS_TOKEN)
            return true;
        if (k1 == SyntaxKind.SLASH_TOKEN && k2 == SyntaxKind.EQUAL_TOKEN)
            return true;
        if (k1 == SyntaxKind.SLASH_TOKEN && k2 == SyntaxKind.EQUAL_EQUAL_TOKEN)
            return true;
        if (k1 == SyntaxKind.SLASH_TOKEN && k2 == SyntaxKind.SLASH_EQUALS_TOKEN)
            return true;
        if (k1 == SyntaxKind.AMPERSAND_TOKEN && k2 == SyntaxKind.EQUAL_TOKEN)
            return true;
        if (k1 == SyntaxKind.AMPERSAND_TOKEN && k2 == SyntaxKind.EQUAL_EQUAL_TOKEN)
            return true;
        if (k1 == SyntaxKind.AMPERSAND_TOKEN && k2 == SyntaxKind.AMPERSAND_EQUALS_TOKEN)
            return true;
        if (k1 == SyntaxKind.PIPE_TOKEN && k2 == SyntaxKind.EQUAL_TOKEN)
            return true;
        if (k1 == SyntaxKind.PIPE_TOKEN && k2 == SyntaxKind.EQUAL_EQUAL_TOKEN)
            return true;
        if (k1 == SyntaxKind.PIPE_TOKEN && k2 == SyntaxKind.PIPE_EQUALS_TOKEN)
            return true;
        if (k1 == SyntaxKind.HAT_TOKEN && k2 == SyntaxKind.EQUAL_TOKEN)
            return true;
        if (k1 == SyntaxKind.HAT_TOKEN && k2 == SyntaxKind.EQUAL_EQUAL_TOKEN)
            return true;
        if (k1 == SyntaxKind.HAT_TOKEN && k2 == SyntaxKind.HAT_EQUALS_TOKEN)
            return true;
        return k1 == SyntaxKind.EXCLAMATION_TOKEN && k2 == SyntaxKind.EQUAL_EQUAL_TOKEN;
    }

}