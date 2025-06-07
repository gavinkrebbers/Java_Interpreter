package Evaluator;

import EvalObject.EvalObject;
import java.util.List;

public class BuiltinEval implements EvalObject {

    public BuiltinFunction fn;

    public BuiltinEval(BuiltinFunction fn) {
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
