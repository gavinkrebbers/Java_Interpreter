package ast;

import Token.Token;

public class LetStatement implements Statement {

    private Token token;
    public Identifier identifier;
    public Expression value;

    public LetStatement(Token token) {
        this.token = token;
    }

    public void setIdentifier(Identifier ident) {
        this.identifier = ident;
    }

    public void setValue(Expression val) {
        this.value = val;
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
        sb.append(TokenLiteral()).append(" ");
        if (identifier != null) {
            sb.append(identifier.toString()).append(" = ");
        }
        if (value != null) {
            sb.append(value.toString());
        }
        sb.append(";");
        return sb.toString();
    }
}
