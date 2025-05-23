package Compiler;

import EvalObject.EvalObject;
import code.Instructions;
import java.util.List;

public class Bytecode {

    public Instructions instructions;
    public List<EvalObject> constants;

    public Bytecode() {
    }

    public Bytecode(Instructions instructions, List<EvalObject> constants) {
        this.instructions = instructions;
        this.constants = constants;
    }

    public Instructions getInstructions() {
        return instructions;
    }

    public void setInstructions(Instructions instructions) {
        this.instructions = instructions;
    }

    public List<EvalObject> getConstants() {
        return constants;
    }

    public void setConstants(List<EvalObject> constants) {
        this.constants = constants;
    }

}
