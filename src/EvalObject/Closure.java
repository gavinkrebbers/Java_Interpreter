package EvalObject;

import java.util.ArrayList;
import java.util.List;

public class Closure implements EvalObject {

    public CompiledFunction function;
    public List<EvalObject> free;

    public Closure(CompiledFunction function, List<EvalObject> free) {
        this.function = function;
        this.free = free;
    }

    public Closure(CompiledFunction function) {
        this.function = function;
        this.free = new ArrayList<>();
    }

    public Closure() {
        this.free = new ArrayList<>();

    }

    @Override
    public String type() {
        return "CLOSURE";
    }

    @Override
    public String inspect() {
        return String.format("Closure[%p]", this);
    }
}
