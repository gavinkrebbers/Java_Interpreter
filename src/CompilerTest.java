
import Compiler.Bytecode;
import Compiler.Compiler;
import Compiler.CompilerError;
import EvalObject.EvalObject;
import EvalObject.IntegerObj;
import Lexer.Lexer;
import Parser.Parser;
import ast.Program;
import code.Code;
import code.Instructions;
import code.Opcode;
import org.junit.Test;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompilerTest {

    static class CompilerTestCase {

        String input;
        List<Object> expectedConstants;
        List<byte[]> expectedInstructions;

        CompilerTestCase(String input, List<Object> expectedConstants, List<byte[]> expectedInstructions) {
            this.input = input;
            this.expectedConstants = expectedConstants;
            this.expectedInstructions = expectedInstructions;
        }
    }

    @Test
    public void testIntegerArithmetic() {
        Code codeInstance = new Code();
        List<CompilerTestCase> tests = Arrays.asList(
                new CompilerTestCase(
                        "1 + 2",
                        Arrays.asList(1, 2),
                        Arrays.asList(
                                codeInstance.Make(Code.OpConstant, 0),
                                codeInstance.Make(Code.OpConstant, 1)
                        )
                )
        );
        runCompilerTests(tests);
    }

    @Test
    public void testInstructionsString() {
        Code codeInstance = new Code();
        List<byte[]> instructions = Arrays.asList(
                codeInstance.Make(Code.OpConstant, 1),
                codeInstance.Make(Code.OpConstant, 2),
                codeInstance.Make(Code.OpConstant, 65535)
        );
        String expected = "0000 OpConstant 1\n"
                + "0003 OpConstant 2\n"
                + "0006 OpConstant 65535\n";
        int totalLength = 0;
        for (byte[] ins : instructions) {
            totalLength += ins.length;
        }
        byte[] concatted = new byte[totalLength];
        int pos = 0;
        for (byte[] ins : instructions) {
            System.arraycopy(ins, 0, concatted, pos, ins.length);
            pos += ins.length;
        }
        code.Instructions inst = new code.Instructions(concatted);
        assertEquals("instructions wrongly formatted.\nwant=" + expected + "\ngot=" + inst.toString(), expected, inst.toString());
    }

    private void runCompilerTests(List<CompilerTestCase> tests) {
        for (CompilerTestCase tt : tests) {
            Program program = parse(tt.input);
            Compiler compiler = new Compiler();
            try {
                compiler.compile(program);

            } catch (CompilerError e) {
                System.out.println("execution error" + e);
                return;
            }
            Bytecode bytecode = compiler.bytecode();
            try {
                testInstructions(tt.expectedInstructions, bytecode.instructions.getInstructions());
            } catch (Exception e) {
                fail("testInstructions failed: " + e.getMessage());
            }
            try {
                testConstants(tt.expectedConstants, bytecode.constants);
            } catch (Exception e) {
                fail("testConstants failed: " + e.getMessage());
            }
        }
    }

    private void testInstructions(List<byte[]> expected, byte[] actual) throws Exception {
        byte[] concatted = concatInstructions(expected);
        if (actual.length != concatted.length) {
            throw new Exception(String.format(
                    "wrong instructions length.\nwant=\n%s\ngot =\n%s",
                    new code.Instructions(concatted).toString(),
                    new code.Instructions(actual).toString()
            ));
        }
        for (int i = 0; i < concatted.length; i++) {
            if (actual[i] != concatted[i]) {
                throw new Exception(String.format(
                        "wrong instruction at %d.\nwant=\n%s\ngot =\n%s",
                        i,
                        new code.Instructions(concatted).toString(),
                        new code.Instructions(actual).toString()
                ));
            }
        }
    }

    private byte[] concatInstructions(List<byte[]> instructionsList) {
        int totalLength = 0;
        for (byte[] ins : instructionsList) {
            totalLength += ins.length;
        }
        byte[] out = new byte[totalLength];
        int pos = 0;
        for (byte[] ins : instructionsList) {
            System.arraycopy(ins, 0, out, pos, ins.length);
            pos += ins.length;
        }
        return out;
    }

    private void testConstants(List<Object> expected, List<EvalObject> actual) throws Exception {
        if (expected.size() != actual.size()) {
            throw new Exception(String.format("wrong number of constants. got=%d, want=%d", actual.size(), expected.size()));
        }
        for (int i = 0; i < expected.size(); i++) {
            Object constant = expected.get(i);
            if (constant != null && constant instanceof Integer) {
                testIntegerObject(((Integer) constant).longValue(), actual.get(i));
            } else if (constant != null) {
                throw new Exception("unhandled constant type: " + constant.getClass());
            } else {
                throw new Exception("constant is null");
            }
        }
    }

    private void testIntegerObject(long expected, EvalObject actual) throws Exception {
        if (actual == null || !(actual instanceof IntegerObj)) {
            throw new Exception(String.format("object is not IntegerObj. got=%s (%s)", actual == null ? "null" : actual.getClass(), actual));
        }
        IntegerObj result = (IntegerObj) actual;
        if (result.getValue() != expected) {
            throw new Exception(String.format("object has wrong value. got=%d, want=%d", result.getValue(), expected));
        }
    }

    private Program parse(String input) {
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        return parser.parseProgram();
    }
}
