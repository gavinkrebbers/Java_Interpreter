
import code.Code;
import code.Instructions;
import org.junit.Test;

import code.Opcode;

import static org.junit.Assert.*;

public class CodeTest {

    @Test
    public void testMake() {
        Object[][] tests = new Object[][]{
            {Code.OpConstant, new int[]{65534}, new byte[]{Code.OpConstant.getValue(), (byte) 255, (byte) 254}},
            {Code.OpAdd, new int[]{}, new byte[]{Code.OpAdd.getValue()}},
            {Code.OpGetLocal, new int[]{255}, new byte[]{Code.OpGetLocal.getValue(), (byte) 255}},
            {Code.OpClosure, new int[]{65534, 255}, new byte[]{Code.OpClosure.getValue(), (byte) 255, (byte) 254, (byte) 255}},};

        for (Object[] test : tests) {
            code.Opcode op = (code.Opcode) test[0];
            int[] operands = (int[]) test[1];
            byte[] expected = (byte[]) test[2];
            byte[] instruction = code.Code.Make(op, operands);
            assertEquals("instruction has wrong length. want=" + expected.length + ", got=" + instruction.length,
                    expected.length, instruction.length);
            for (int i = 0; i < expected.length; i++) {
                assertEquals("wrong byte at pos " + i + ". want=" + expected[i] + ", got=" + instruction[i],
                        expected[i], instruction[i]);
            }
        }
    }

    @Test
    public void testReadOperands() throws Exception {
        Object[][] tests = new Object[][]{
            {Code.OpConstant, new int[]{65535}, 2},
            {Code.OpGetLocal, new int[]{255}, 1},
            {Code.OpClosure, new int[]{65535, 255}, 3},};

        for (Object[] test : tests) {
            code.Opcode op = (code.Opcode) test[0];
            int[] operands = (int[]) test[1];
            int expectedBytesRead = (int) test[2];

            byte[] instruction = code.Code.Make(op, operands);
            code.Code codeInstance = new code.Code();
            code.Definition def = codeInstance.lookup(op.getValue());
            int[] operandsRead = new int[def.getOperandWidths().length];

            int n = 0;
            int offset = 0;
            for (int i = 0; i < def.getOperandWidths().length; i++) {
                int width = def.getOperandWidths()[i];
                if (width == 2) {
                    operandsRead[i] = Instructions.readUint16(new byte[]{instruction[1 + offset], instruction[2 + offset]});
                } else if (width == 1) {
                    operandsRead[i] = instruction[1 + offset] & 0xFF;
                }
                offset += width;
                n += width;
            }

            assertEquals("n wrong. want=" + expectedBytesRead + ", got=" + n, expectedBytesRead, n);
            for (int i = 0; i < operands.length; i++) {
                assertEquals("operand " + i + " wrong. want=" + operands[i] + ", got=" + operandsRead[i],
                        operands[i], operandsRead[i]);
            }
        }
    }

    @Test
    public void testInstructionsString() throws Exception {
        byte[] ins1 = Code.Make(Code.OpAdd);
        byte[] ins2 = Code.Make(Code.OpGetLocal, 1);
        byte[] ins3 = Code.Make(Code.OpConstant, 2);
        byte[] ins4 = Code.Make(Code.OpConstant, 65535);
        byte[] ins5 = Code.Make(Code.OpClosure, 65535, 255);

        int totalLength = ins1.length + ins2.length + ins3.length + ins4.length + ins5.length;
        byte[] concatted = new byte[totalLength];
        System.arraycopy(ins1, 0, concatted, 0, ins1.length);
        System.arraycopy(ins2, 0, concatted, ins1.length, ins2.length);
        System.arraycopy(ins3, 0, concatted, ins1.length + ins2.length, ins3.length);
        System.arraycopy(ins4, 0, concatted, ins1.length + ins2.length + ins3.length, ins4.length);
        System.arraycopy(ins5, 0, concatted, ins1.length + ins2.length + ins3.length + ins4.length, ins5.length);

        Instructions instructions = new Instructions(concatted);
        String expected = "0000 OpAdd\n"
                + "0001 OpGetLocal 1\n"
                + "0003 OpConstant 2\n"
                + "0006 OpConstant 65535\n"
                + "0009 OpClosure 65535 255\n";

        assertEquals("instructions wrongly formatted.\nwant=\n" + expected + "\ngot=\n" + instructions.toString(),
                expected, instructions.toString());
    }
}
