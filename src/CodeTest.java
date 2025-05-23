
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
}
