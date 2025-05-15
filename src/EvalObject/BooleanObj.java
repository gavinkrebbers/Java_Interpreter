package EvalObject;

public class BooleanObj implements EvalObject, Hashable {

    public boolean value;

    public BooleanObj(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return this.value;
    }

    @Override
    public HashKey generateHashKey() {
        if (value) {
            return new HashKey("BOOLEAN", 1);

        }
        return new HashKey("BOOLEAN", 0);

    }

    @Override
    public String type() {
        return "BOOLEAN";
    }

    @Override
    public String inspect() {
        return Boolean.toString(this.value);
    }

}
