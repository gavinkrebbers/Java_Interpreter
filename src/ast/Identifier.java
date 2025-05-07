package ast;

import Token.Token;

public class Identifier implements Statement {

    private Token token;
    public String value;

    @Override
    public String TokenLiteral() {
        return token.literal;
    }

    @Override
    public void statementNode() {

    }
}
