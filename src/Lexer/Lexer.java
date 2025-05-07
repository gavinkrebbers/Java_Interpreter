package Lexer;

import Token.Token;
import Token.TokenType;

public class Lexer {

    public String input;
    public int position;
    public int readPosition;
    public char ch;

    public Lexer(String input) {
        this.input = input;
        this.position = 0;
        this.readPosition = 0;
        this.ch = '\0';
        readChar();
    }

    public Token nextToken() {

        Token tok = null;
        skipWhitespace();
        if (ch == '5') {
            System.out.println("weee");
        }
        switch (ch) {
            case '=':
                tok = new Token(new TokenType(TokenType.ASSIGN), Character.toString(ch));
                break;
            case ';':
                tok = new Token(new TokenType(TokenType.SEMICOLON), Character.toString(ch));
                break;
            case '(':
                tok = new Token(new TokenType(TokenType.LPAREN), Character.toString(ch));
                break;
            case ')':
                tok = new Token(new TokenType(TokenType.RPAREN), Character.toString(ch));
                break;
            case ',':
                tok = new Token(new TokenType(TokenType.COMMA), Character.toString(ch));
                break;
            case '+':
                tok = new Token(new TokenType(TokenType.PLUS), Character.toString(ch));
                break;
            case '{':
                tok = new Token(new TokenType(TokenType.LBRACE), Character.toString(ch));
                break;
            case '}':
                tok = new Token(new TokenType(TokenType.RBRACE), Character.toString(ch));
                break;
            case '\0':
                tok = new Token(new TokenType(TokenType.EOF), "");
                break;
            default:
                if (isLetter(ch)) {
                    if (ch == 'f') {
                        System.out.println("hi");
                    }
                    tok = new Token();
                    tok.literal = readIdentifier();
                    tok.type = tok.lookupIdent(tok.literal);
                    return tok;
                } else if (isNumber(ch)) {
                    tok = new Token();
                    tok.type = new TokenType(TokenType.INT);
                    tok.literal = readNumber();
                    return tok;
                } else {
                    tok = new Token(new TokenType(TokenType.ILLEGAL), Character.toString(ch));

                }

                break;
        }
        readChar();
        return tok;
    }

    public String readIdentifier() {
        String result = "";
        while (isLetter(ch)) {
            result += ch;
            readChar();
        }
        return result;
    }

    public String readNumber() {
        String result = "";
        while (isNumber(ch)) {
            result += Character.toString(ch);
            readChar();
        }
        return result;
    }

    public void skipWhitespace() {
        while (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
            readChar();
        }
    }

    public void readChar() {
        if (readPosition >= input.length()) {
            ch = '\0';
        } else {
            ch = input.charAt(readPosition);
        }
        position = readPosition;
        readPosition++;
    }

    public boolean isLetter(char ch) {
        return Character.isLetter(ch) || ch == '-';

    }

    public boolean isNumber(char ch) {
        return Character.isDigit(ch);
    }

}
