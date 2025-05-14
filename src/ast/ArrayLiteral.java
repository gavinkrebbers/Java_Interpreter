package ast;

import Token.Token;
import java.util.ArrayList;
import java.util.List;

public class ArrayLiteral implements Expression {

    public Token token;
    public List<Expression> elements;

    public ArrayLiteral(Token token) {
        this.token = token;
    }

    public List<Expression> getElements() {
        return elements;
    }

    public void setElements(List<Expression> elements) {
        this.elements = elements;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();

        List<String> elementsStrings = new ArrayList<>();
        for (Expression elem : elements) {
            elementsStrings.add(elem.toString());
        }

        output.append("[");
        output.append(String.join(", ", elementsStrings));
        output.append("]");

        return output.toString();

    }

    @Override
    public String TokenLiteral() {
        return token.literal;
    }

    @Override
    public void expressionNode() {

    }
}
