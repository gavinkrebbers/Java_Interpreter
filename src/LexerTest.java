
import org.junit.Test;

import Lexer.Lexer;
import Token.Token;
import Token.TokenType;

import static org.junit.Assert.assertEquals;

public class LexerTest {

    @Test
    public void testNextToken() {

        String input = """
                       let five = 5;
                       let ten = 10;
                       let add = fn(x, y) {
                       x + y;
                       };
                       let result = add(five, ten);" string :3 "[1,2,3]{"foo" : "bar"}
                       while(){1}""";

        Token[] expectedTokens = {
            new Token(new TokenType(TokenType.LET), "let"),
            new Token(new TokenType(TokenType.IDENT), "five"),
            new Token(new TokenType(TokenType.ASSIGN), "="),
            new Token(new TokenType(TokenType.INT), "5"),
            new Token(new TokenType(TokenType.SEMICOLON), ";"),
            new Token(new TokenType(TokenType.LET), "let"),
            new Token(new TokenType(TokenType.IDENT), "ten"),
            new Token(new TokenType(TokenType.ASSIGN), "="),
            new Token(new TokenType(TokenType.INT), "10"),
            new Token(new TokenType(TokenType.SEMICOLON), ";"),
            new Token(new TokenType(TokenType.LET), "let"),
            new Token(new TokenType(TokenType.IDENT), "add"),
            new Token(new TokenType(TokenType.ASSIGN), "="),
            new Token(new TokenType(TokenType.FUNCTION), "fn"),
            new Token(new TokenType(TokenType.LPAREN), "("),
            new Token(new TokenType(TokenType.IDENT), "x"),
            new Token(new TokenType(TokenType.COMMA), ","),
            new Token(new TokenType(TokenType.IDENT), "y"),
            new Token(new TokenType(TokenType.RPAREN), ")"),
            new Token(new TokenType(TokenType.LBRACE), "{"),
            new Token(new TokenType(TokenType.IDENT), "x"),
            new Token(new TokenType(TokenType.PLUS), "+"),
            new Token(new TokenType(TokenType.IDENT), "y"),
            new Token(new TokenType(TokenType.SEMICOLON), ";"),
            new Token(new TokenType(TokenType.RBRACE), "}"),
            new Token(new TokenType(TokenType.SEMICOLON), ";"),
            new Token(new TokenType(TokenType.LET), "let"),
            new Token(new TokenType(TokenType.IDENT), "result"),
            new Token(new TokenType(TokenType.ASSIGN), "="),
            new Token(new TokenType(TokenType.IDENT), "add"),
            new Token(new TokenType(TokenType.LPAREN), "("),
            new Token(new TokenType(TokenType.IDENT), "five"),
            new Token(new TokenType(TokenType.COMMA), ","),
            new Token(new TokenType(TokenType.IDENT), "ten"),
            new Token(new TokenType(TokenType.RPAREN), ")"),
            new Token(new TokenType(TokenType.SEMICOLON), ";"),
            new Token(new TokenType(TokenType.STRING), " string :3 "),
            new Token(new TokenType(TokenType.LBRACKET), "["),
            new Token(new TokenType(TokenType.INT), "1"),
            new Token(new TokenType(TokenType.COMMA), ","),
            new Token(new TokenType(TokenType.INT), "2"),
            new Token(new TokenType(TokenType.COMMA), ","),
            new Token(new TokenType(TokenType.INT), "3"),
            new Token(new TokenType(TokenType.RBRACKET), "]"),
            new Token(new TokenType(TokenType.LBRACE), "{"),
            new Token(new TokenType(TokenType.STRING), "foo"),
            new Token(new TokenType(TokenType.COLON), ":"),
            new Token(new TokenType(TokenType.STRING), "bar"),
            new Token(new TokenType(TokenType.RBRACE), "}"),
            new Token(new TokenType(TokenType.WHILE), "while"),
            new Token(new TokenType(TokenType.LPAREN), "("),
            new Token(new TokenType(TokenType.RPAREN), ")"),
            new Token(new TokenType(TokenType.LBRACE), "{"),
            new Token(new TokenType(TokenType.INT), "1"),
            new Token(new TokenType(TokenType.RBRACE), "}"),
            new Token(new TokenType(TokenType.EOF), "")
        };

        Lexer lexer = new Lexer(input);

        for (int i = 0; i < expectedTokens.length; i++) {
            Token actualToken = lexer.nextToken();
            Token expectedToken = expectedTokens[i];

            assertEquals("Token type mismatch at index " + i, expectedToken.type.TokenType, actualToken.type.TokenType);
            assertEquals("Token literal mismatch at index " + i, expectedToken.literal, actualToken.literal);
        }
    }
}
