package EvalObject;

public class ErrorObj implements EvalObject {

    public String message;

    public ErrorObj(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String type() {
        return "ERROR";

    }

    @Override
    public String inspect() {
        return "ERROR: " + this.message;
    }
}
