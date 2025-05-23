package vm;

import Compiler.Bytecode;
import EvalObject.EvalObject;
import code.Instructions;
import code.Opcode;
import java.util.List;

public class VM {

    public final byte OPCODE_CONSTANT = 0;
    public final int STACK_SIZE = 2048;

    public List<EvalObject> constants;
    public Instructions instructions;

    public EvalObject[] stack = new EvalObject[STACK_SIZE];

    public int sp = 0;

    public VM(Bytecode bytecode) {
        this.constants = bytecode.constants;
        this.instructions = bytecode.instructions;

    }

    public void run() throws ExecutionError {
        byte[] ins = instructions.getInstructions();
        for (int ip = 0; ip < ins.length; ip++) {
            Opcode op = new Opcode(ins[ip]);
            byte opValue = op.getValue();
            switch (opValue) {
                case OPCODE_CONSTANT:
                    int constIndex = ((ins[ip + 1] & 0xFF) << 8) | (ins[ip + 2] & 0xFF);
                    ip += 2;
                    push(constants.get(constIndex));
                    break;
                default:
                    throw new ExecutionError("unrecognized opcode" + op.getValue());
            }
        }

    }

    public void push(EvalObject obj) throws ExecutionError {
        if (sp >= STACK_SIZE) {
            throw new ExecutionError("stack overflow");
        }
        stack[sp] = obj;
        sp++;

    }

    public EvalObject LastPoppedElement() {
        return stack[sp];
    }

    public EvalObject stackTop() {
        return stack[sp - 1];
    }

}
