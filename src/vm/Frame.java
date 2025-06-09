package vm;

import EvalObject.Closure;
import code.Instructions;

public class Frame {

    // public CompiledFunction function;
    public Closure closure;
    public int ip;
    public int basePointer;

    public Frame(Closure closure, int basePointer) {
        this.closure = closure;
        this.ip = -1;
        this.basePointer = basePointer;
    }

    public Instructions getInstructions() {
        return this.closure.function.instructions;
    }
}
