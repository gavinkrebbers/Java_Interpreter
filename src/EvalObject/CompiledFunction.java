package EvalObject;

import code.Instructions;

public class CompiledFunction implements EvalObject {

    public Instructions instructions;
    public int numLocals;
    public int numArgs;

    public CompiledFunction(Instructions instructions) {
        this.instructions = instructions;
        numLocals = 0;
    }

    public CompiledFunction(Instructions instructions, int numLocals) {
        this.instructions = instructions;
        this.numLocals = numLocals;
    }

    public CompiledFunction(Instructions instructions, int numLocals, int numArgs) {
        this.instructions = instructions;
        this.numLocals = numLocals;
        this.numArgs = numArgs;
    }

    public Instructions getInstructions() {
        return instructions;
    }

    @Override
    public String type() {
        return "COMPILED_FUNCTION_OBJ";
    }

    @Override
    public String inspect() {
        return String.format("CompiledFunction[%s]", this);
    }

}
