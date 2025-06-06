package ast;

import Token.Token;

public class Identifier implements Expression {

    private Token token;
    public String value;

    public Identifier(Token token, String value) {
        this.token = token;
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public String TokenLiteral() {
        return token.literal;
    }

    @Override
    public void expressionNode() {

    }
}
