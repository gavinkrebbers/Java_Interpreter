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
}
