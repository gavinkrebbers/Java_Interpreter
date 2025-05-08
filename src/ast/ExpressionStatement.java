package ast;

import Token.Token;

public class ExpressionStatement implements Statement {

    public Token token;
    public Expression expression;

    public ExpressionStatement(Token token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return expression.toString();
    }

    @Override
    public String TokenLiteral() {
        return token.literal;
    }

    @Override
    public void statementNode() {

    }
}
