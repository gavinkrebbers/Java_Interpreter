package ast;

import Token.Token;
import java.util.List;

public class FunctionLiteral implements Expression {

    public Token token;
    public List<Identifier> parameters;
    public BlockStatement body;

    public FunctionLiteral(Token token) {
        this.token = token;
    }

    @Override
    public String TokenLiteral() {
        return token.literal;
    }

    public Token getToken() {
        return token;
    }

    public List<Identifier> getParameters() {
        return parameters;
    }

    public BlockStatement getBody() {
        return body;
    }

    @Override
    public void expressionNode() {

    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(token.literal);
        builder.append("(");
        if (parameters != null) {
            for (int i = 0; i < parameters.size(); i++) {
                builder.append(parameters.get(i).toString());
                if (i < parameters.size() - 1) {
                    builder.append(", ");
                }
            }
        }
        builder.append(") ");
        builder.append(body != null ? body.toString() : "{}");
        return builder.toString();
    }

}
