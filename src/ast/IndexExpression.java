package ast;

import Token.Token;

public class IndexExpression implements Expression {

    public Token token;
    public Expression left;
    public Expression index;

    public IndexExpression(Token token, Expression left) {
        this.token = token;
        this.left = left;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("(");
        out.append(left.toString());
        out.append("[");
        out.append(index.toString());
        out.append("])");
        return out.toString();
    }

    @Override
    public String TokenLiteral() {
        return token.literal;
    }

    @Override
    public void expressionNode() {

    }

}
