package codeanalysis.lexer;

import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxToken;
import codeanalysis.syntax.SyntaxTree;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class LexerTest {

    @ParameterizedTest
    @MethodSource("provideTokens")
    void lexToken(SyntaxKind kind,String text) {
        Lexer lexer = new Lexer(text);
        SyntaxToken token = lexer.lex();
        assertEquals(kind,token.getKind());
        assertEquals(text,token.getText());
    }

    @ParameterizedTest
    @MethodSource("provideTokenPairs")
    void lexTokenPairs(SyntaxKind k1,String t1,SyntaxKind k2,String t2) {
        String text = t1+t2;
        List<SyntaxToken> tokens= SyntaxTree.parseTokens(text);
        assertEquals(tokens.size(),2);
        assertEquals(tokens.get(0).getKind(),k1);
        assertEquals(tokens.get(0).getText(),t1);
        assertEquals(tokens.get(1).getKind(),k2);
        assertEquals(tokens.get(1).getText(),t2);
    }

    @ParameterizedTest
    @MethodSource("provideTokenPairsWithSeparator")
    void lexTokenWithSeparators(SyntaxKind k1,String t1,
                                SyntaxKind separatorKind,String separatorText,
                                SyntaxKind k2,String t2){
        String text = t1+separatorText+t2;
        List<SyntaxToken> tokens= SyntaxTree.parseTokens(text);
        assertEquals(tokens.size(),3);
        assertEquals(tokens.get(0).getKind(),k1);
        assertEquals(tokens.get(0).getText(),t1);
        assertEquals(tokens.get(1).getKind(),separatorKind);
        assertEquals(tokens.get(1).getText(),separatorText);
        assertEquals(tokens.get(2).getKind(),k2);
        assertEquals(tokens.get(2).getText(),t2);
    }

    private static Stream<Arguments> provideTokens(){
        return Stream.concat(getTokens(),getSeparator());
    }

    private static Stream<Arguments> provideTokenPairs(){
        List<Arguments> arguments = getTokens().toList();
        List<Arguments> pairs = new ArrayList<>();
        for (Arguments a1: arguments ){
            SyntaxKind k1 = (SyntaxKind) a1.get()[0];
            String t1 = (String) a1.get()[1];
            for (Arguments a2: arguments ){
                SyntaxKind k2 = (SyntaxKind) a2.get()[0];
                String t2 = (String) a2.get()[1];
                if(!requiresSeparator(k1,k2))
                    pairs.add(Arguments.of(k1,t1,k2,t2));
            }
        }
        return pairs.stream();
    }

    private static Stream<Arguments> provideTokenPairsWithSeparator(){
        List<Arguments> arguments = getTokens().toList();
        List<Arguments> pairs = new ArrayList<>();
        for (Arguments a1: arguments ){
            SyntaxKind k1 = (SyntaxKind) a1.get()[0];
            String t1 = (String) a1.get()[1];
            for (Arguments a2: arguments ){
                SyntaxKind k2 = (SyntaxKind) a2.get()[0];
                String t2 = (String) a2.get()[1];
                if(requiresSeparator(k1,k2))
                    for(Arguments sa:getSeparator().toList()) {
                        SyntaxKind sk = (SyntaxKind) sa.get()[0];
                        String st = (String) sa.get()[1];
                        pairs.add(Arguments.of(k1, t1,sk,st, k2, t2));
                    }
            }
        }
        return pairs.stream();
    }

    private static Stream<Arguments> getTokens() {
        return Stream.of(
                Arguments.of(SyntaxKind.PLUS_TOKEN,"+"),
                Arguments.of(SyntaxKind.NUMBER_TOKEN,"1"),
                Arguments.of(SyntaxKind.MINUS_TOKEN,"-"),
                Arguments.of(SyntaxKind.SLASH_TOKEN,"/"),
                Arguments.of(SyntaxKind.STAR_TOKEN,"*"),
                Arguments.of(SyntaxKind.OPEN_PARENTHESIS_TOKEN,"("),
                Arguments.of(SyntaxKind.CLOSE_PARENTHESIS_TOKEN,")"),
                Arguments.of(SyntaxKind.EQUAL_TOKEN,"="),
                Arguments.of(SyntaxKind.IDENTIFIER_TOKEN,"v"),
                Arguments.of(SyntaxKind.IDENTIFIER_TOKEN,"variable"),
                Arguments.of(SyntaxKind.EQUAL_EQUAL_TOKEN,"=="),
                Arguments.of(SyntaxKind.EXCLAMATION_EQUAL_TOKEN,"!="),
                Arguments.of(SyntaxKind.EXCLAMATION_TOKEN,"!"),
                Arguments.of(SyntaxKind.AMPERSAND_AMPERSAND_TOKEN,"&&"),
                Arguments.of(SyntaxKind.PIPE_PIPE_TOKEN,"||"),
                Arguments.of(SyntaxKind.FALSE_KEYWORD,"false"),
                Arguments.of(SyntaxKind.TRUE_KEYWORD,"true")
        );
    }

    private static Stream<Arguments> getSeparator(){
        return Stream.of(
                Arguments.of(SyntaxKind.WHITESPACE_TOKEN," "),
                Arguments.of(SyntaxKind.WHITESPACE_TOKEN,"  "),
                Arguments.of(SyntaxKind.WHITESPACE_TOKEN,"\r"),
                Arguments.of(SyntaxKind.WHITESPACE_TOKEN,"\n"),
                Arguments.of(SyntaxKind.WHITESPACE_TOKEN,"\n\r")
        );
    }

    private static  boolean requiresSeparator(SyntaxKind k1,SyntaxKind k2){
        if(k1.toString().endsWith("KEYWORD") && k2.toString().endsWith("KEYWORD"))
            return true;
        if(k1.toString().endsWith("KEYWORD") && k2 == SyntaxKind.IDENTIFIER_TOKEN)
            return true;
        if(k1 == SyntaxKind.IDENTIFIER_TOKEN  && k2.toString().endsWith("KEYWORD"))
            return true;
        if(k1 == SyntaxKind.IDENTIFIER_TOKEN && k2 == SyntaxKind.IDENTIFIER_TOKEN)
            return true;
        if(k1 == SyntaxKind.NUMBER_TOKEN && k2 == SyntaxKind.NUMBER_TOKEN)
            return true;
        if(k1 == SyntaxKind.EXCLAMATION_TOKEN && k2 == SyntaxKind.EQUAL_TOKEN)
            return true;
        if(k1 == SyntaxKind.EQUAL_TOKEN && k2 == SyntaxKind.EQUAL_TOKEN)
            return true;
        if(k1 == SyntaxKind.EQUAL_TOKEN && k2 == SyntaxKind.EQUAL_EQUAL_TOKEN)
            return true;
        if(k1 == SyntaxKind.EXCLAMATION_TOKEN && k2 == SyntaxKind.EXCLAMATION_TOKEN)
            return true;
        if(k1 == SyntaxKind.EXCLAMATION_TOKEN && k2 == SyntaxKind.EQUAL_EQUAL_TOKEN)
            return true;
        return  false;
    }

}