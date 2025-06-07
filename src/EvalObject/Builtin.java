package EvalObject;

import java.util.List;

public class Builtin implements EvalObject {

    public BuiltinFunction fn;
    public String name;

    public Builtin() {
    }

    public Builtin(String name, BuiltinFunction fn) {
        this.fn = fn;
        this.name = name;
    }

    @Override
    public String type() {
        return "Builtin";
    }

    @Override
    public String inspect() {
        return "builtin function";
    }

    @FunctionalInterface
    public interface BuiltinFunction {

        EvalObject apply(List<EvalObject> args);
    }

}
