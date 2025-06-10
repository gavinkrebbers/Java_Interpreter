package Compiler;

public class CompilationScope {

    // public Instructions instructions;
    public byte[] instructions;
    public EmittedInstruction lastInstruction;
    public EmittedInstruction prevInstruction;

    public CompilationScope() {
        this.instructions = new byte[0];
        this.lastInstruction = new EmittedInstruction();
        this.prevInstruction = new EmittedInstruction();
    }

    public CompilationScope(byte[] instructions, EmittedInstruction lastInstruction, EmittedInstruction prevInstruction) {
        this.instructions = instructions;
        this.lastInstruction = lastInstruction;
        this.prevInstruction = prevInstruction;
    }
}
