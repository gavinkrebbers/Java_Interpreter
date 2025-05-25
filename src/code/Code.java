package code;

import java.util.HashMap;
import java.util.Map;

public class Code {

    public static final byte OpConstantValue = 1;
    public static final byte OpPopValue = 2;
    public static final byte OpAddValue = 3;
    public static final byte OpSubValue = 4;
    public static final byte OpMulValue = 5;
    public static final byte OpDivValue = 6;
    public static final byte OpTrueValue = 7;
    public static final byte OpFalseValue = 8;
    public static final byte OpEqualValue = 9;
    public static final byte OpNotEqualValue = 10;
    public static final byte OpGreaterThanValue = 11;
    public static final byte OpMinusValue = 12;
    public static final byte OpBangValue = 13;
    public static final byte OpJumpNotTruthyValue = 14;
    public static final byte OpJumpValue = 15;
    public static final byte OpNullValue = 16;

    public static final Opcode OpConstant = new Opcode(OpConstantValue);
    public static final Opcode OpPop = new Opcode(OpPopValue);
    public static final Opcode OpAdd = new Opcode(OpAddValue);
    public static final Opcode OpSub = new Opcode(OpSubValue);
    public static final Opcode OpMul = new Opcode(OpMulValue);
    public static final Opcode OpDiv = new Opcode(OpDivValue);
    public static final Opcode OpTrue = new Opcode(OpTrueValue);
    public static final Opcode OpFalse = new Opcode(OpFalseValue);
    public static final Opcode OpEqual = new Opcode(OpEqualValue);
    public static final Opcode OpNotEqual = new Opcode(OpNotEqualValue);
    public static final Opcode OpGreaterThan = new Opcode(OpGreaterThanValue);
    public static final Opcode OpMinus = new Opcode(OpMinusValue);
    public static final Opcode OpBang = new Opcode(OpBangValue);
    public static final Opcode OpJumpNotTruthy = new Opcode(OpJumpNotTruthyValue);
    public static final Opcode OpJump = new Opcode(OpJumpValue);
    public static final Opcode OpNull = new Opcode(OpNullValue);

    private static final Map<Opcode, Definition> definitions = new HashMap<>();

    static {
        addDefinition(OpConstant, "OpConstant", 2);
        addDefinition(OpPop, "OpPop");
        addDefinition(OpAdd, "OpAdd");
        addDefinition(OpSub, "OpSub");
        addDefinition(OpMul, "OpMul");
        addDefinition(OpDiv, "OpDiv");
        addDefinition(OpTrue, "OpTrue");
        addDefinition(OpFalse, "OpFalse");
        addDefinition(OpEqual, "OpEqual");
        addDefinition(OpNotEqual, "OpNotEqual");
        addDefinition(OpGreaterThan, "OpGreaterThan");
        addDefinition(OpMinus, "OpMinus");
        addDefinition(OpBang, "OpBang");
        addDefinition(OpJumpNotTruthy, "OpJumpNotTruthy", 2);
        addDefinition(OpJump, "OpJump", 2);
        addDefinition(OpNull, "OpNull");
    }

    private static void addDefinition(Opcode opcode, String name, int... operandWidths) {
        definitions.put(opcode, new Definition(name, operandWidths));
    }

    public static byte[] Make(Opcode op, int... operands) {
        Definition def = definitions.get(op);
        if (def == null) {
            return new byte[0];
        }

        int instructionLen = 1;
        for (int width : def.getOperandWidths()) {
            instructionLen += width;
        }

        byte[] instruction = new byte[instructionLen];
        instruction[0] = op.getValue();

        int offset = 1;
        for (int i = 0; i < operands.length; i++) {
            int width = def.getOperandWidths()[i];
            int operand = operands[i];

            if (width == 2) {
                instruction[offset] = (byte) ((operand >> 8) & 0xFF);
                instruction[offset + 1] = (byte) (operand & 0xFF);
            } else {
                throw new IllegalArgumentException("Unsupported operand width: " + width);
            }
            offset += width;
        }

        return instruction;
    }

    public Definition lookup(byte op) throws Exception {
        Opcode opcode = new Opcode(op);
        Definition def = definitions.get(opcode);
        if (def == null) {
            throw new Exception("opcode " + (op & 0xFF) + " undefined");
        }
        return def;
    }
}
