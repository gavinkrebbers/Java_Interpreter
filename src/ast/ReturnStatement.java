package ast;

import Token.Token;

public class ReturnStatement implements Statement {

    public Token token;
    public Expression returnValue;

    public ReturnStatement(Token token, Expression returnValue) {
        this.token = token;
        this.returnValue = returnValue;
    }

    public ReturnStatement(Token token) {
        this.token = token;

    }

    public ReturnStatement() {

    }

    @Override
    public String toString() {
        return token.literal + " " + (returnValue != null ? returnValue.toString() : "") + ";";
    }

    @Override
    public String TokenLiteral() {
        return token.literal;
    }

    @Override
    public void statementNode() {

    }

}
