package EvalObject;

public class StringObj implements EvalObject {

    public String value;

    public StringObj(String value) {
        this.value = value;
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
