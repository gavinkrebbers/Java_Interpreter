package Compiler;

import code.Instructions;

public class CompilationScope {

    public Instructions instructions;
    public EmittedInstruction lastInstruction;
    public EmittedInstruction prevInstruction;

    public CompilationScope() {
        this.instructions = new Instructions();
        this.lastInstruction = new EmittedInstruction();
        this.prevInstruction = new EmittedInstruction();
    }

    public CompilationScope(Instructions instructions, EmittedInstruction lastInstruction, EmittedInstruction prevInstruction) {
        this.instructions = instructions;
        this.lastInstruction = lastInstruction;
        this.prevInstruction = prevInstruction;
    }
}
