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
    private static final Map<Opcode, Definition> definitions = new HashMap<>();

    static {
        definitions.put(OpConstant, new Definition("OpConstant", new int[]{2}));
        definitions.put(OpPop, new Definition("OpPop", new int[]{}));

        definitions.put(OpAdd, new Definition("OpAdd", new int[]{}));
        definitions.put(OpSub, new Definition("OpSub", new int[]{}));
        definitions.put(OpMul, new Definition("OpMul", new int[]{}));
        definitions.put(OpDiv, new Definition("OpDiv", new int[]{}));

        definitions.put(OpTrue, new Definition("OpTrue", new int[]{}));
        definitions.put(OpFalse, new Definition("OpFalse", new int[]{}));

        definitions.put(OpEqual, new Definition("OpEqual", new int[]{}));
        definitions.put(OpNotEqual, new Definition("OpNotEqual", new int[]{}));
        definitions.put(OpGreaterThan, new Definition("OpGreaterThan", new int[]{}));

        definitions.put(OpMinus, new Definition("OpMinus", new int[]{}));
        definitions.put(OpBang, new Definition("OpBang", new int[]{}));

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
        for (int index = 0; index < operands.length; index++) {
            int width = def.getOperandWidths()[index];
            int operand = operands[index];
            switch (width) {
                case 2:
                    instruction[offset] = (byte) ((operand >> 8) & 0xFF);
                    instruction[offset + 1] = (byte) (operand & 0xFF);
                    break;

                default:
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
