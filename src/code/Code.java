package code;

import java.util.HashMap;
import java.util.Map;

public class Code {

    public static final Opcode OpConstant = new Opcode(1);
    public static final Opcode OpAdd = new Opcode(2);
    private static final Map<Opcode, Definition> definitions = new HashMap<>();

    static {
        definitions.put(OpConstant, new Definition("OpConstant", new int[]{2}));
        definitions.put(OpAdd, new Definition("OpAdd", new int[]{}));
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
