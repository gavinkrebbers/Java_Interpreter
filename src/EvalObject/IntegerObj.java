package EvalObject;

public class IntegerObj implements EvalObject {

    public int value;

    public IntegerObj(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String type() {
        return "INTEGER";

    }

    @Override
    public String inspect() {
        return Integer.toString(this.value);
    }

}
