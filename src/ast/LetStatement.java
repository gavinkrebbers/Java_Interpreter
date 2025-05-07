package ast;

import Token.Token;

public class LetStatement implements Statement {

    private Token token;
    public Identifier identifier;

    @Override
    public String TokenLiteral() {
        return token.literal;
    }

    @Override
    public void statementNode() {

    }
}
