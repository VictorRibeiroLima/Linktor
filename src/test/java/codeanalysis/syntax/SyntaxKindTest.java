package codeanalysis.syntax;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SyntaxKindTest {

    @ParameterizedTest
    @MethodSource("provideKind")
    void getText(SyntaxKind kind){
        String text = SyntaxKind.getText(kind);
        if(text == null)
            return;
        List<SyntaxToken> tokens = SyntaxTree.parseTokens(text);
        assertEquals(1,tokens.size());
        assertEquals(kind,tokens.get(0).getKind());
    }

    private static Stream<Arguments> provideKind() {
        SyntaxKind[] kinds = SyntaxKind.values();
        List<Arguments> args = new ArrayList<>();
        for (SyntaxKind kind:kinds) {
            args.add(Arguments.of(kind));
        }
        return args.stream();
    }
}