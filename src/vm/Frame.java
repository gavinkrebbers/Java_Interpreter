package vm;

import EvalObject.CompiledFunction;
import code.Instructions;

public class Frame {

    public CompiledFunction function;
    public int ip;
    public int basePointer;

    public Frame(CompiledFunction function, int basePointer) {
        this.function = function;
        this.ip = -1;
        this.basePointer = basePointer;
    }

    public Instructions getInstructions() {
        return this.function.instructions;
    }
}
