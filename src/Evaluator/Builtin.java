package Evaluator;

import java.util.List;

import EvalObject.EvalObject;

public class Builtin implements EvalObject {

    public BuiltinFunction fn;

    public Builtin(BuiltinFunction fn) {
        this.fn = fn;
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
