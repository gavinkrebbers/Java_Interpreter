package ast;

import Token.Token;

public class IfExpression implements Expression {

    public Token token;
    public Expression condition;
    public BlockStatement consequence;
    public BlockStatement alternative;

    public IfExpression(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    public Expression getCondition() {
        return condition;
    }

    public BlockStatement getConsequence() {
        return consequence;
    }

    public BlockStatement getAlternative() {
        return alternative;
    }

    @Override
    public String TokenLiteral() {
        return token.literal;
    }

    @Override
    public void expressionNode() {

    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("if ");
        builder.append(condition.toString());
        builder.append(" ");
        builder.append(consequence.toString());
        if (alternative != null) {
            builder.append(" else ");
            builder.append(alternative.toString());
        }
        return builder.toString();
    }
}
