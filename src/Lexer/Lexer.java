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

        switch (ch) {

            case '/':
                tok = new Token(new TokenType(TokenType.SLASH), Character.toString(ch));
                break;
            case '>':
                tok = new Token(new TokenType(TokenType.GT), Character.toString(ch));
                break;
            case '<':
                tok = new Token(new TokenType(TokenType.LT), Character.toString(ch));
                break;
            case '*':
                tok = new Token(new TokenType(TokenType.ASTERISK), Character.toString(ch));
                break;
            case '"':
                StringBuilder output = new StringBuilder();
                readChar();
                while (ch != '"') {
                    output.append(ch);
                    readChar();
                }
                tok = new Token(new TokenType(TokenType.STRING), output.toString());
                break;
            case '!':
                if (peekChar() == '=') {
                    tok = new Token(new TokenType(TokenType.NOT_EQ), "!=");
                    readChar();
                } else {
                    tok = new Token(new TokenType(TokenType.BANG), Character.toString(ch));
                }
                break;
            case '-':
                tok = new Token(new TokenType(TokenType.MINUS), Character.toString(ch));
                break;
            case '=':
                if (peekChar() == '=') {
                    tok = new Token(new TokenType(TokenType.EQ), "==");
                    readChar();
                    break;
                }
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
            case '[':
                tok = new Token(new TokenType(TokenType.LBRACKET), Character.toString(ch));
                break;
            case ']':
                tok = new Token(new TokenType(TokenType.RBRACKET), Character.toString(ch));
                break;
            case '\0':
                tok = new Token(new TokenType(TokenType.EOF), "");
                break;

            default:
                if (isLetter(ch)) {

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

    public char peekChar() {
        if (readPosition >= input.length()) {
            return 0;
        }

        return input.charAt(readPosition);
    }

    public boolean isLetter(char ch) {
        return Character.isLetter(ch) || ch == '_';

    }

    public boolean isNumber(char ch) {
        return Character.isDigit(ch);
    }

}
