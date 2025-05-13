package EvalObject;

public class ReturnObj implements EvalObject {

    public EvalObject value;

    public ReturnObj(EvalObject value) {
        this.value = value;
    }

    @Override
    public String type() {
        return "RETURN_VALUE";

    }

    @Override
    public String inspect() {
        return value.inspect();
    }

}
