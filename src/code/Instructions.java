package code;

public class Instructions {

    public byte[] instructions;

    public Instructions(byte[] instructions) {
        this.instructions = instructions;
    }

    public Instructions() {
        this.instructions = new byte[0];
    }

    public byte[] getInstructions() {
        return instructions;
    }

    public void setInstructions(byte[] instructions) {
        this.instructions = instructions;
    }

    public static int readUint16(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 8) | (bytes[1] & 0xFF);
    }

    public static int readUint8(byte[] bytes) {
        return bytes[0];
    }

    public void addInstruction(byte[] newInstruction) {
        if (instructions == null) {
            instructions = newInstruction.clone();
            return;
        }
        byte[] combined = new byte[instructions.length + newInstruction.length];
        System.arraycopy(instructions, 0, combined, 0, instructions.length);
        System.arraycopy(newInstruction, 0, combined, instructions.length, newInstruction.length);
        instructions = combined;
    }

    @Override
    public String toString() {
        if (instructions == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < instructions.length) {
            try {
                code.Code codeInstance = new code.Code();
                code.Definition def = codeInstance.lookup(instructions[i]);
                int[] operandWidths = def.getOperandWidths();
                int operandsCount = operandWidths.length;
                int[] operands = new int[operandsCount];
                int offset = i + 1;
                sb.append(String.format("%04d %s", i, def.getName()));
                for (int j = 0; j < operandsCount; j++) {
                    int width = operandWidths[j];
                    if (width == 2) {
                        operands[j] = readUint16(new byte[]{instructions[offset], instructions[offset + 1]});
                    }
                    if (width == 1) {
                        operands[j] = readUint8(new byte[]{instructions[offset]});

                    }
                    offset += width;
                }
                for (int operand : operands) {
                    sb.append(" ").append(operand);
                }
                sb.append("\n");
                i = offset;
            } catch (Exception e) {
                sb.append(String.format("%04d UNKNOWN\n", i));
                break;
            }
        }
        return sb.toString();
    }
}
