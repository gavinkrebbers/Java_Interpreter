package ast;

import Token.Token;

public class PrefixExpression implements Expression {

    public Token token;
    public String operator;
    public Expression right;

    public PrefixExpression(Token token, String operator, Expression right) {
        this.token = token;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public String toString() {
        return "(" + operator + right.toString() + ")";
    }

    public PrefixExpression(Token token, String operator) {
        this.token = token;
        this.operator = operator;

    }

    @Override
    public String TokenLiteral() {
        return token.literal;
    }

    @Override
    public void expressionNode() {

    }
}
