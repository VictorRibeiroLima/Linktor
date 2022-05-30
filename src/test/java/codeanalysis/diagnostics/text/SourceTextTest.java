package codeanalysis.diagnostics.text;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SourceTextTest {

    @ParameterizedTest
    @MethodSource("provideText")
    void sourceTextIncludeLines(String text, int expectedNumberOfLines) {
        SourceText source = SourceText.from(text);
        assertEquals(expectedNumberOfLines, source.getLines().size());
    }

    @ParameterizedTest
    @MethodSource("provideLineIndex")
    void getLineIndex(String text, int linePosition, int expectedIndex) {
        SourceText source = SourceText.from(text);
        int lineIndex = source.getLineIndex(linePosition);
        assertEquals(expectedIndex, lineIndex);
    }

    static Stream<Arguments> provideText() {
        return Stream.of(
                Arguments.of(".", 1),
                Arguments.of(".\n", 2),
                Arguments.of(".\n\n.", 3),
                Arguments.of(".\r", 2),
                Arguments.of(".\r.\r.", 3),
                Arguments.of(".\r\n.", 2),
                Arguments.of(".\n\r.", 3)
        );
    }

    static Stream<Arguments> provideLineIndex() {
        String text = ".first line\nsecond line\nthird line";
        return Stream.of(
                Arguments.of(text, 4, 0),
                Arguments.of(text, 8, 0),
                Arguments.of(text, 14, 1),
                Arguments.of(text, 18, 1),
                Arguments.of(text, 25, 2),
                Arguments.of(text, 28, 2)
        );
    }
}