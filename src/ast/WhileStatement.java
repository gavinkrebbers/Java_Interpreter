package ast;

import Token.Token;

public class WhileStatement implements Statement {

    public Token token;
    public Expression condition;
    public BlockStatement body;

    public WhileStatement(BlockStatement body, Expression condition, Token token) {
        this.body = body;
        this.condition = condition;
        this.token = token;
    }

    public WhileStatement() {
    }

    public WhileStatement(Token token) {
        this.token = token;
    }

    @Override
    public String TokenLiteral() {
        return token.literal;
    }

    @Override
    public void statementNode() {

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("while");
        sb.append("(");
        sb.append(condition.toString());
        sb.append(")");
        sb.append("{");
        sb.append(body.toString());
        sb.append("}");

        return sb.toString();
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public Expression getCondition() {
        return condition;
    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    public BlockStatement getBody() {
        return body;
    }

    public void setBody(BlockStatement body) {
        this.body = body;
    }
}
