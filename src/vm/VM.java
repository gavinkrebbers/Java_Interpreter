package vm;

import Compiler.Bytecode;
import EvalObject.ArrayObj;
import EvalObject.BooleanObj;
import EvalObject.Builtin;
import EvalObject.Builtins;
import EvalObject.Closure;
import EvalObject.CompiledFunction;
import EvalObject.EvalObject;
import EvalObject.HashKey;
import EvalObject.HashObj;
import EvalObject.Hashable;
import EvalObject.IntegerObj;
import EvalObject.NullObj;
import EvalObject.Pair;
import EvalObject.StringObj;
import code.Code;
import code.Instructions;
import code.Opcode;
import java.lang.classfile.Instruction;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class VM {

    public final int STACK_SIZE = 2048;
    public static final int GLOBALS_SIZE = 65536;

    public List<EvalObject> constants;

    public EvalObject[] stack = new EvalObject[STACK_SIZE];
    public List<EvalObject> globals = new ArrayList<>();

    public int sp = 0;

    public Deque<Frame> frames;
    public int framesIndex;

    public static final BooleanObj TRUE = new BooleanObj(true);
    public static final BooleanObj FALSE = new BooleanObj(false);
    public static final NullObj NULL_OBJ = new NullObj();

    public VM(Bytecode bytecode) {
        CompiledFunction mainFunction = new CompiledFunction(bytecode.instructions);
        Closure mainClosure = new Closure(mainFunction);
        Frame mainFrame = new Frame(mainClosure, 0);

        this.constants = bytecode.constants;
        this.frames = new ArrayDeque<>();
        this.frames.add(mainFrame);
        this.framesIndex = 1;
    }

    public VM(Bytecode bytecode, List<EvalObject> globals) {
        CompiledFunction mainFunction = new CompiledFunction(bytecode.instructions);
        Closure mainClosure = new Closure(mainFunction);
        Frame mainFrame = new Frame(mainClosure, 0);

        this.constants = bytecode.constants;
        this.globals = globals;

        this.frames = new ArrayDeque<>();
        this.frames.add(mainFrame);
        this.framesIndex = 1;
    }

    public void run() throws ExecutionError {

        int ip = 0;
        Instructions ins;
        Opcode op;

        while (currentFrame().ip < currentFrame().getInstructions().instructions.length - 1) {
            currentFrame().ip++;
            ip = currentFrame().ip;
            ins = currentFrame().getInstructions();
            op = new Opcode(ins.instructions[ip]);
            String helper = currentFrame().getInstructions().toString();
            byte opValue = op.value;
            switch (opValue) {
                case Code.OpGetFreeValue:
                    int freeIndex = Instructions.readUint8(new byte[]{currentFrame().getInstructions().instructions[ip + 1]});
                    currentFrame().ip++;
                    Closure currentClosure = currentFrame().closure;
                    push(currentClosure.free.get(freeIndex));
                    break;
                case Code.OpClosureValue:
                    int funcIndex = Instructions.readUint16(new byte[]{ins.instructions[ip + 1], ins.instructions[ip + 2]});
                    int numFree = Instructions.readUint8(new byte[]{currentFrame().getInstructions().instructions[ip + 3]});
                    currentFrame().ip += 3;
                    pushClosure(funcIndex, numFree);
                    break;
                case Code.OpGetBuiltinValue:
                    int builtinIndex = Instructions.readUint8(new byte[]{currentFrame().getInstructions().instructions[ip + 1]});
                    currentFrame().ip++;
                    push(Builtins.builtins[builtinIndex]);
                    break;
                case Code.OpConstantValue:
                    int constIndex = Instructions.readUint16(new byte[]{ins.instructions[ip + 1], ins.instructions[ip + 2]});
                    currentFrame().ip += 2;
                    // this is the error 
                    // sp gets set to -1????
                    push(constants.get(constIndex));
                    break;
                case Code.OpAddValue, Code.OpSubValue, Code.OpMulValue, Code.OpDivValue:
                    executeBinaryOperation(opValue);
                    break;
                case Code.OpArrayValue:
                    executeArray(ip);
                    break;
                case Code.OpHashValue:
                    executeHash(ip);
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
                case Code.OpJumpNotTruthyValue:
                    int landingIndex = Instructions.readUint16(new byte[]{ins.instructions[ip + 1], ins.instructions[ip + 2]});
                    currentFrame().ip += 2;
                    EvalObject condition = pop();
                    if (!isTruthy(condition)) {
                        currentFrame().ip = landingIndex - 1;
                    }
                    break;
                case Code.OpJumpValue:
                    currentFrame().ip = Instructions.readUint16(new byte[]{ins.instructions[ip + 1], ins.instructions[ip + 2]}) - 1;
                    break;
                case Code.OpNullValue:
                    push(NULL_OBJ);
                    break;
                case Code.OpGetGlobalValue:
                    int globalsIndex = Instructions.readUint16(new byte[]{ins.instructions[ip + 1], ins.instructions[ip + 2]});
                    currentFrame().ip += 2;
                    push(globals.get(globalsIndex));
                    break;
                case Code.OpSetGlobalValue:
                    int globalIndex = Instructions.readUint16(new byte[]{ins.instructions[ip + 1], ins.instructions[ip + 2]});
                    currentFrame().ip += 2;

                    EvalObject evalObject = pop();
                    setAtIndex(globals, globalIndex, evalObject);
                    break;
                case Code.OpIndexValue:
                    executeIndexExpression();
                    break;
                case Code.OpCallValue:
                    int numArgs = Instructions.readUint8(new byte[]{currentFrame().getInstructions().instructions[ip + 1]});
                    currentFrame().ip++;
                    executeCall(numArgs);
                    break;
                case Code.OpReturnObjectValue:
                    EvalObject pop = pop();
                    Frame frame = popFrame();
                    sp = frame.basePointer - 1;
                    push(pop);
                    break;
                case Code.OpReturnValue:
                    frame = popFrame();
                    sp = frame.basePointer - 1;

                    push(NULL_OBJ);
                    break;
                case Code.OpSetLocalValue:
                    int localIndex = Instructions.readUint8(new byte[]{currentFrame().getInstructions().instructions[ip + 1]});
                    currentFrame().ip += 1;

                    stack[currentFrame().basePointer + localIndex] = pop();
                    break;
                case Code.OpGetLocalValue:
                    localIndex = Instructions.readUint8(new byte[]{currentFrame().getInstructions().instructions[ip + 1]});
                    currentFrame().ip += 1;
                    Frame localFrame = currentFrame();
                    push(stack[localFrame.basePointer + localIndex]);
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
        } else if (left instanceof StringObj leftString && right instanceof StringObj rightString) {
            executeBinaryStringOperation(opValue, leftString, rightString);
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

    public void executeBinaryStringOperation(byte opValue, StringObj left, StringObj right) throws ExecutionError {
        if (opValue != Code.OpAddValue) {
            throw new ExecutionError("unknown string operator");
        }
        push(new StringObj(left.value + right.value));
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

    public int executeArray(int ip) throws ExecutionError {

        byte[] ins = currentFrame().getInstructions().instructions;
        int arrSize = Instructions.readUint16(new byte[]{ins[ip + 1], ins[ip + 2]});
        currentFrame().ip += 2;
        int startIndex = this.sp - arrSize;
        if (startIndex < 0) {
            throw new ExecutionError("stack underflow while creating arr");

        }
        List<EvalObject> elements = new LinkedList<>();
        for (int index = 0; index < arrSize; index++) {
            elements.add(stack[startIndex + index]);
        }
        sp = startIndex;
        ArrayObj arr = new ArrayObj(elements);
        push(arr);
        return ip;
    }

    public int executeHash(int ip) throws ExecutionError {
        byte[] ins = currentFrame().getInstructions().instructions;
        int pairCount = Instructions.readUint16(new byte[]{ins[ip + 1], ins[ip + 2]});
        currentFrame().ip += 2;
        int startingIndex = sp - pairCount;
        int endingIndex = sp;
        Map<HashKey, Pair> hashMap = new HashMap<>();
        for (int i = startingIndex; i < endingIndex; i += 2) {
            EvalObject key = stack[i];
            EvalObject value = stack[i + 1];
            if (!(key instanceof Hashable)) {
                throw new ExecutionError("not valid key type");
            }
            HashKey hashKey = ((Hashable) key).generateHashKey();
            hashMap.put(hashKey, new Pair(key, value));
        }

        sp = startingIndex;
        push(new HashObj(hashMap));
        return ip;
    }

    public void executeIndexExpression() throws ExecutionError {
        EvalObject index = pop();
        EvalObject left = pop();
        if (left instanceof ArrayObj arrayObj && index instanceof IntegerObj integerObj) {
            if (integerObj.value >= arrayObj.elements.size() || integerObj.value < 0) {
                push(NULL_OBJ);
                return;
            }
            push(arrayObj.elements.get(integerObj.value));
        } else if (left instanceof HashObj hashObj) {
            if (!(index instanceof Hashable)) {
                throw new ExecutionError("not valid index operator for hash: " + index.type());
            }

            HashKey hashKey = ((Hashable) index).generateHashKey();
            Pair pair = hashObj.map.get(hashKey);
            push(pair != null ? pair.value : NULL_OBJ);

        } else {
            throw new ExecutionError("invalid index expression");
        }
    }

    public void executeCall(int numArgs) throws ExecutionError {

        EvalObject callee = stack[sp - numArgs - 1];

        if (callee instanceof Closure closure) {
            Frame frame = new Frame(closure, sp - numArgs);
            pushFrame(frame);
            sp = frame.basePointer + closure.function.numLocals;

        } else if (callee instanceof Builtin builtin) {
            List<EvalObject> args = new ArrayList<>();
            for (int i = sp - numArgs; i < sp; i++) {
                args.add(stack[i]);
            }
            EvalObject result = builtin.fn.apply(args);
            sp = sp - numArgs - 1;
            push(result);
        } else {
            throw new ExecutionError("calling non function");
        }

    }

    public void pushClosure(int functionIndex, int numFree) throws ExecutionError {
        EvalObject constant = constants.get(functionIndex);
        if (!(constant instanceof CompiledFunction)) {
            throw new ExecutionError("not a funciton");
        }
        CompiledFunction compiledFunction = (CompiledFunction) constant;
        List<EvalObject> freeVars = new ArrayList<>();
        for (int i = 0; i < numFree; i++) {
            // TODO keep an eye on this 
            freeVars.add(stack[sp - numFree + i]);
        }
        sp = sp - numFree;
        Closure closure = new Closure(compiledFunction, freeVars);
        push(closure);
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
            return this.TRUE;

        }
        return this.FALSE;
    }

    public boolean isTruthy(EvalObject obj) {
        if (obj instanceof BooleanObj bool) {
            if (bool.value) {
                return true;
            } else {
                return false;
            }
        } else if (obj instanceof NullObj) {
            return false;
        }
        return true;

    }

    public static void setAtIndex(List<EvalObject> globals, int index, EvalObject value) {
        while (globals.size() <= index) {
            globals.add(NULL_OBJ);
        }
        globals.set(index, value);
    }

    public Frame currentFrame() {
        return frames.peek();
    }

    public void pushFrame(Frame newFrame) {
        frames.push(newFrame);
        framesIndex++;
    }

    public Frame popFrame() {
        framesIndex--;

        return frames.pop();
    }
}
