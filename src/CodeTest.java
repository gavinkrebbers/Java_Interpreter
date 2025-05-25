
import code.Code;
import org.junit.Test;

import code.Opcode;

import static org.junit.Assert.*;

public class CodeTest {

    @Test
    public void testMake() {
        Opcode op = Code.OpConstant;
        int[] operands = new int[]{65534};
        byte[] expected = new byte[]{op.getValue(), (byte) 255, (byte) 254};

        byte[] instruction = Code.Make(op, operands);

        assertEquals("Instruction has wrong length", expected.length, instruction.length);

        for (int i = 0; i < expected.length; i++) {
            assertEquals("Wrong byte at pos " + i, expected[i], instruction[i]);
        }
    }

    @Test
    public void testMakeMultiple() {
        // OpAdd is not defined in your Code.java, so let's define its value as 1 for this test
        Object[][] tests = new Object[][]{
            {code.Code.OpConstant, new int[]{65534}, new byte[]{code.Code.OpConstant.getValue(), (byte) 255, (byte) 254}},
            {Code.OpAdd, new int[]{}, new byte[]{Code.OpAdd.getValue()}},};
        for (Object[] test : tests) {
            code.Opcode op = (code.Opcode) test[0];
            int[] operands = (int[]) test[1];
            byte[] expected = (byte[]) test[2];
            byte[] instruction = code.Code.Make(op, operands);
            assertEquals("instruction has wrong length. want=" + expected.length + ", got=" + instruction.length, expected.length, instruction.length);
            for (int i = 0; i < expected.length; i++) {
                assertEquals("wrong byte at pos " + i + ". want=" + expected[i] + ", got=" + instruction[i], expected[i], instruction[i]);
            }
        }
    }

    @Test
    public void testInstructionsString() throws Exception {
        byte[] ins1 = code.Code.Make(code.Code.OpAdd);
        byte[] ins2 = code.Code.Make(code.Code.OpConstant, 2);
        byte[] ins3 = code.Code.Make(code.Code.OpConstant, 65535);
        int totalLength = ins1.length + ins2.length + ins3.length;
        byte[] concatted = new byte[totalLength];
        System.arraycopy(ins1, 0, concatted, 0, ins1.length);
        System.arraycopy(ins2, 0, concatted, ins1.length, ins2.length);
        System.arraycopy(ins3, 0, concatted, ins1.length + ins2.length, ins3.length);
        code.Instructions instructions = new code.Instructions(concatted);
        String expected = "0000 OpAdd\n"
                + "0001 OpConstant 2\n"
                + "0004 OpConstant 65535\n";
        assertEquals("instructions wrongly formatted.\nwant=" + expected + "\ngot=" + instructions.toString(), expected, instructions.toString());
    }

    @Test
    public void testReadOperands() throws Exception {
        code.Opcode op = code.Code.OpConstant;
        int[] operands = new int[]{65535};
        byte[] instruction = code.Code.Make(op, operands);
        code.Code codeInstance = new code.Code();
        code.Definition def = codeInstance.lookup(op.getValue());
        int[] operandsRead = new int[def.getOperandWidths().length];
        int n = 0;
        int offset = 0;
        for (int i = 0; i < def.getOperandWidths().length; i++) {
            int width = def.getOperandWidths()[i];
            if (width == 2) {
                operandsRead[i] = ((instruction[1 + offset] & 0xFF) << 8) | (instruction[2 + offset] & 0xFF);
            }
            offset += width;
            n += width;
        }
        assertEquals("n wrong. want=2, got=" + n, 2, n);
        assertEquals("operand wrong. want=65535, got=" + operandsRead[0], 65535, operandsRead[0]);
    }
}
