package ast;

import Token.Token;

public class StringLiteral implements Expression {

    public Token token;
    public String value;

    public StringLiteral(Token token, String value) {
        this.token = token;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
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
