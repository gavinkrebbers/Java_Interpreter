package ast;

import Token.Token;
import java.util.List;

public class CallExpression implements Expression {

    public Token token;
    public Expression function;
    public List<Expression> arguments;

    public CallExpression(Token token, Expression function) {
        this.function = function;
        this.token = token;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(function.toString());
        builder.append("(");
        if (arguments != null && !arguments.isEmpty()) {
            for (int i = 0; i < arguments.size(); i++) {
                builder.append(arguments.get(i).toString());
                if (i < arguments.size() - 1) {
                    builder.append(", ");
                }
            }
        }
        builder.append(")");
        return builder.toString();
    }

    @Override
    public String TokenLiteral() {
        return token.literal;
    }

    @Override
    public void expressionNode() {

    }
}
