package vm;

import EvalObject.CompiledFunction;
import code.Instructions;

public class Frame {

    public CompiledFunction function;
    public int ip;

    public Frame(CompiledFunction function) {
        this.function = function;
        this.ip = -1;
    }

    public Instructions getInstructions() {
        return this.function.instructions;
    }
}
