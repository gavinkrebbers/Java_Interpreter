package ast;

import Token.Token;

public class IntegerLiteral implements Expression {

    public Token token;
    public int value;

    public IntegerLiteral(Token token, int value) {
        this.token = token;
        this.value = value;
    }

    public Token getToken() {
        return token;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return TokenLiteral();
    }

    @Override
    public String TokenLiteral() {
        return token.literal;
    }

    @Override
    public void expressionNode() {

    }
}
