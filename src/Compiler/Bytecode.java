package Compiler;

import EvalObject.EvalObject;
import java.util.List;

public class Bytecode {

    public byte[] instructions;
    public List<EvalObject> constants;

    public Bytecode() {
    }

    public Bytecode(byte[] instructions, List<EvalObject> constants) {
        this.instructions = instructions;
        this.constants = constants;
    }

    public byte[] getInstructions() {
        return instructions;
    }

    public void setInstructions(byte[] instructions) {
        this.instructions = instructions;
    }

    public List<EvalObject> getConstants() {
        return constants;
    }

    public void setConstants(List<EvalObject> constants) {
        this.constants = constants;
    }

}
