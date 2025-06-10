package EvalObject;

public class CompiledFunction implements EvalObject {

    public byte[] instructions;
    public int numLocals;
    public int numArgs;

    public CompiledFunction(byte[] instructions) {
        this.instructions = instructions;
        numLocals = 0;
    }

    public CompiledFunction(byte[] instructions, int numLocals) {
        this.instructions = instructions;
        this.numLocals = numLocals;
    }

    public CompiledFunction(byte[] instructions, int numLocals, int numArgs) {
        this.instructions = instructions;
        this.numLocals = numLocals;
        this.numArgs = numArgs;
    }

    public byte[] getInstructions() {
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
