package EvalObject;

public class NullObj implements EvalObject {

    public NullObj() {

    }

    @Override
    public String type() {
        return "NULL";
    }

    @Override
    public String inspect() {
        return "null";
    }
}
