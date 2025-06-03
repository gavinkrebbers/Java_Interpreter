package EvalObject;

import code.Instructions;

public class CompiledFunction implements EvalObject {

    public Instructions instructions;

    public CompiledFunction(Instructions instructions) {
        this.instructions = instructions;
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
