package ast;

import Token.Token;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HashLiteral implements Expression {

    public Token token;
    public Map<Expression, Expression> pairs;

    public HashLiteral(Token token) {
        this.token = token;
        this.pairs = new HashMap<>();
    }

    @Override
    public String toString() {
        return TokenLiteral();
    }

    @Override
    public String TokenLiteral() {
        StringBuilder output = new StringBuilder();

        List<String> pairsString = new LinkedList<>();
        pairs.forEach((key, value) -> {
            pairsString.add(key.toString());
            pairsString.add(":");
            pairsString.add(value.toString());
        });

        output.append("{");
        output.append(String.join(",", pairsString));
        output.append("}");

        return output.toString();
    }

    @Override
    public void expressionNode() {

    }
}
