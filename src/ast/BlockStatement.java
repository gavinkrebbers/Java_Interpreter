package ast;

import Token.Token;
import java.util.ArrayList;
import java.util.List;

public class BlockStatement implements Statement {

    public Token token;
    public List<Statement> statements;

    public BlockStatement(Token token) {
        this.token = token;
        this.statements = new ArrayList<Statement>();
    }

    public void addStatement(Statement stmt) {
        statements.add(stmt);
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
        StringBuilder out = new StringBuilder();
        for (Statement statement : statements) {
            out.append(statement.toString());
        }
        return out.toString();
    }
}
