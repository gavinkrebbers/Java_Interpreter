package vm;

import Compiler.Bytecode;
import EvalObject.BooleanObj;
import EvalObject.EvalObject;
import EvalObject.IntegerObj;
import code.Code;
import code.Instructions;
import code.Opcode;
import java.util.List;

public class VM {

    public final int STACK_SIZE = 2048;

    public static final BooleanObj TRUE = new BooleanObj(true);
    public static final BooleanObj FALSE = new BooleanObj(false);
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
                case Code.OpConstantValue:
                    int constIndex = ((ins[ip + 1] & 0xFF) << 8) | (ins[ip + 2] & 0xFF);
                    ip += 2;
                    push(constants.get(constIndex));
                    break;
                case Code.OpAddValue, Code.OpSubValue, Code.OpMulValue, Code.OpDivValue:
                    executeBinaryOperation(opValue);

                    break;
                case Code.OpPopValue:
                    pop();
                    break;
                case Code.OpTrueValue:
                    push(TRUE);
                    break;
                case Code.OpFalseValue:
                    push(FALSE);
                    break;
                case Code.OpEqualValue, Code.OpNotEqualValue, Code.OpGreaterThanValue:
                    executeComparisonOperator(opValue);
                    break;
                case Code.OpBangValue:
                    executeBangOperator();
                    break;
                case Code.OpMinusValue:
                    executeMinusOperator();
                    break;

                default:
                    throw new ExecutionError("unrecognized opcode" + op.getValue());
            }
        }

    }

    public void executeBangOperator() throws ExecutionError {
        EvalObject right = pop();
        if (right instanceof BooleanObj rightBool) {
            push(nativeBoolToBoolObject(!rightBool.value));
        } else {
            push(FALSE);
        }
    }

    public void executeMinusOperator() throws ExecutionError {
        EvalObject right = pop();
        if (right instanceof IntegerObj rightInt) {
            push(new IntegerObj(-1 * rightInt.value));
        } else {
            throw new ExecutionError("unsupported type for negation : " + right.type());
        }
    }

    public void executeBinaryOperation(byte opValue) throws ExecutionError {
        EvalObject right = pop();
        EvalObject left = pop();
        if (left instanceof IntegerObj leftInt && right instanceof IntegerObj rightInt) {
            executeBinaryIntegerOperation(opValue, leftInt, rightInt);
        } else {
            throw new ExecutionError(String.format("unsupported types for binary operation: %s %s", left, right));

        }
    }

    public void executeBinaryIntegerOperation(byte opValue, IntegerObj left, IntegerObj right) throws ExecutionError {
        int leftInt = left.getValue();
        int rightInt = right.getValue();
        int result;
        switch (opValue) {
            case Code.OpAddValue:
                result = leftInt + rightInt;
                break;
            case Code.OpSubValue:
                result = leftInt - rightInt;
                break;
            case Code.OpMulValue:
                result = leftInt * rightInt;
                break;
            case Code.OpDivValue:
                result = leftInt / rightInt;
                break;
            default:
                throw new ExecutionError("unknown integer operator");
        }
        push(new IntegerObj(result));
    }

    public void executeComparisonOperator(byte op) throws ExecutionError {
        EvalObject right = pop();
        EvalObject left = pop();
        if (left instanceof IntegerObj leftInt && right instanceof IntegerObj rightInt) {
            executeIntegerComparionOperator(op, leftInt, rightInt);
        } else if (left instanceof BooleanObj leftBool && right instanceof BooleanObj rightBool) {
            switch (op) {
                case Code.OpEqualValue:
                    push(nativeBoolToBoolObject(leftBool.value == rightBool.value));
                    break;
                case Code.OpNotEqualValue:
                    push(nativeBoolToBoolObject(leftBool.value != rightBool.value));
                    break;
                default:
                    throw new AssertionError();
            }
        } else {
            throw new ExecutionError("an error occured in comparison");
        }
    }

    public void executeIntegerComparionOperator(byte op, IntegerObj leftInt, IntegerObj rightInt) throws ExecutionError {
        switch (op) {
            case Code.OpEqualValue:
                push(nativeBoolToBoolObject(leftInt.value == rightInt.value));
                break;
            case Code.OpNotEqualValue:
                push(nativeBoolToBoolObject(leftInt.value != rightInt.value));
                break;

            case Code.OpGreaterThanValue:
                push(nativeBoolToBoolObject(leftInt.value > rightInt.value));

                break;
            default:
                throw new AssertionError();
        }
    }

    public void push(EvalObject obj) throws ExecutionError {
        if (sp >= STACK_SIZE) {
            throw new ExecutionError("stack overflow");
        }
        stack[sp] = obj;
        sp++;

    }

    public EvalObject lastPoppedElement() {
        return stack[sp];
    }

    public EvalObject pop() {
        EvalObject obj = stack[sp - 1];
        sp--;
        return obj;
    }

    public EvalObject LastPoppedElement() {
        return stack[sp];
    }

    public EvalObject stackTop() {
        return stack[sp - 1];
    }

    public BooleanObj nativeBoolToBoolObject(boolean input) {
        if (input) {
            return TRUE;

        }
        return FALSE;
    }

}
