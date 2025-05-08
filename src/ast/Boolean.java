package ast;

import Token.Token;

public class Boolean implements Expression {

    public Token token;
    public boolean value;

    public Boolean(Token token, boolean value) {
        this.token = token;
        this.value = value;
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
