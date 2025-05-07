package ast;

import Token.Token;

public class ReturnStatement implements Statement {

    public Token token;
    public Expression returnValue;

    public ReturnStatement(Token token, Expression returnValue) {
        this.token = token;
        this.returnValue = returnValue;
    }

    public ReturnStatement() {

    }

    @Override
    public String TokenLiteral() {
        return token.literal;
    }

    @Override
    public void statementNode() {

    }

}
