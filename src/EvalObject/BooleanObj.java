package EvalObject;

public class BooleanObj implements EvalObject {

    public boolean value;

    public BooleanObj(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return this.value;
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
