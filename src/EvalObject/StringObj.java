package EvalObject;

import Token.TokenType;

public class StringObj implements EvalObject, Hashable {

    public String value;

    public StringObj(String value) {
        this.value = value;
    }

    @Override
    public HashKey generateHashKey() {
        return new HashKey(TokenType.STRING, value.hashCode());
    }

    @Override
    public String type() {
        return "STRING";

    }

    @Override
    public String inspect() {
        return value;
    }

}
