package Compiler;

import code.Opcode;

public class EmittedInstruction {

    public Opcode opcode;
    public int position;

    public EmittedInstruction() {
    }

    public EmittedInstruction(Opcode opcode, int position) {
        this.opcode = opcode;
        this.position = position;
    }
}
