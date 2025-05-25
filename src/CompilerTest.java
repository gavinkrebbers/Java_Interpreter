
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

    private void runCompilerTests(List<CompilerTestCase> tests) {
        for (CompilerTestCase tt : tests) {
            Program program = parse(tt.input);
            Compiler compiler = new Compiler();
            try {
                compiler.compile(program);

            } catch (CompilerError e) {
                fail("compilation error: " + e.getMessage());
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

    @Test
    public void testIntegerArithmetic() {
        Code codeInstance = new Code();
        List<CompilerTestCase> tests = Arrays.asList(
                new CompilerTestCase(
                        "1 + 2",
                        Arrays.asList(1, 2),
                        Arrays.asList(
                                Code.Make(Code.OpConstant, 0),
                                Code.Make(Code.OpConstant, 1),
                                Code.Make(Code.OpAdd),
                                Code.Make(Code.OpPop)
                        )
                ),
                new CompilerTestCase(
                        "1; 2",
                        Arrays.asList(1, 2),
                        Arrays.asList(
                                Code.Make(Code.OpConstant, 0),
                                Code.Make(Code.OpPop),
                                Code.Make(Code.OpConstant, 1),
                                Code.Make(Code.OpPop)
                        )
                ),
                new CompilerTestCase(
                        "2 - 1",
                        Arrays.asList(2, 1),
                        Arrays.asList(
                                Code.Make(Code.OpConstant, 0),
                                Code.Make(Code.OpConstant, 1),
                                Code.Make(Code.OpSub),
                                Code.Make(Code.OpPop)
                        )
                ),
                new CompilerTestCase(
                        "2 * 3",
                        Arrays.asList(2, 3),
                        Arrays.asList(
                                Code.Make(Code.OpConstant, 0),
                                Code.Make(Code.OpConstant, 1),
                                Code.Make(Code.OpMul),
                                Code.Make(Code.OpPop)
                        )
                ),
                new CompilerTestCase(
                        "6 / 2",
                        Arrays.asList(6, 2),
                        Arrays.asList(
                                Code.Make(Code.OpConstant, 0),
                                Code.Make(Code.OpConstant, 1),
                                Code.Make(Code.OpDiv),
                                Code.Make(Code.OpPop)
                        )
                ),
                new CompilerTestCase(
                        "-1",
                        Arrays.asList(1),
                        Arrays.asList(
                                Code.Make(Code.OpConstant, 0),
                                Code.Make(Code.OpMinus),
                                Code.Make(Code.OpPop)
                        )
                )
        );
        runCompilerTests(tests);
    }

    @Test
    public void testBooleanExpressions() {
        List<CompilerTestCase> tests = Arrays.asList(
                new CompilerTestCase(
                        "true",
                        Arrays.asList(),
                        Arrays.asList(
                                Code.Make(Code.OpTrue),
                                Code.Make(Code.OpPop)
                        )
                ),
                new CompilerTestCase(
                        "false",
                        Arrays.asList(),
                        Arrays.asList(
                                Code.Make(Code.OpFalse),
                                Code.Make(Code.OpPop)
                        )
                ),
                new CompilerTestCase(
                        "1 > 2",
                        Arrays.asList(1, 2),
                        Arrays.asList(
                                Code.Make(Code.OpConstant, 0),
                                Code.Make(Code.OpConstant, 1),
                                Code.Make(Code.OpGreaterThan),
                                Code.Make(Code.OpPop)
                        )
                ),
                new CompilerTestCase(
                        "1 < 2",
                        Arrays.asList(2, 1),
                        Arrays.asList(
                                Code.Make(Code.OpConstant, 0),
                                Code.Make(Code.OpConstant, 1),
                                Code.Make(Code.OpGreaterThan),
                                Code.Make(Code.OpPop)
                        )
                ),
                new CompilerTestCase(
                        "1 == 2",
                        Arrays.asList(1, 2),
                        Arrays.asList(
                                Code.Make(Code.OpConstant, 0),
                                Code.Make(Code.OpConstant, 1),
                                Code.Make(Code.OpEqual),
                                Code.Make(Code.OpPop)
                        )
                ),
                new CompilerTestCase(
                        "1 != 2",
                        Arrays.asList(1, 2),
                        Arrays.asList(
                                Code.Make(Code.OpConstant, 0),
                                Code.Make(Code.OpConstant, 1),
                                Code.Make(Code.OpNotEqual),
                                Code.Make(Code.OpPop)
                        )
                ),
                new CompilerTestCase(
                        "true == false",
                        Arrays.asList(),
                        Arrays.asList(
                                Code.Make(Code.OpTrue),
                                Code.Make(Code.OpFalse),
                                Code.Make(Code.OpEqual),
                                Code.Make(Code.OpPop)
                        )
                ),
                new CompilerTestCase(
                        "true != false",
                        Arrays.asList(),
                        Arrays.asList(
                                Code.Make(Code.OpTrue),
                                Code.Make(Code.OpFalse),
                                Code.Make(Code.OpNotEqual),
                                Code.Make(Code.OpPop)
                        )
                ),
                new CompilerTestCase(
                        "!true",
                        Arrays.asList(),
                        Arrays.asList(
                                Code.Make(Code.OpTrue),
                                Code.Make(Code.OpBang),
                                Code.Make(Code.OpPop)
                        )
                )
        );
        runCompilerTests(tests);

    }

    @Test
    public void testConditionals() {
        List<CompilerTestCase> tests = Arrays.asList(
                new CompilerTestCase(
                        "if (true) { 10 }; 3333;",
                        Arrays.asList(10, 3333),
                        Arrays.asList(
                                // 0000 OpTrue
                                Code.Make(Code.OpTrue),
                                // 0001 OpJumpNotTruthy with operand (skip 7 bytes)
                                Code.Make(Code.OpJumpNotTruthy, 10),
                                // 0004 OpConstant 0 (10)
                                Code.Make(Code.OpConstant, 0),
                                // 0007 OpJump, jump to 11
                                Code.Make(Code.OpJump, 11),
                                // 0010 OpNull
                                Code.Make(Code.OpNull),
                                // 0011 OpPop (end of if block)
                                Code.Make(Code.OpPop),
                                // 0012 OpConstant 1 (3333)
                                Code.Make(Code.OpConstant, 1),
                                // 0015 OpPop (end of statement)
                                Code.Make(Code.OpPop)
                        )
                ),
                new CompilerTestCase(
                        "if (true) { 10 } else { 20 }; 3333;",
                        Arrays.asList(10, 20, 3333),
                        Arrays.asList(
                                // 0000
                                Code.Make(Code.OpTrue),
                                // 0001
                                Code.Make(Code.OpJumpNotTruthy, 10),
                                // 0004
                                Code.Make(Code.OpConstant, 0),
                                // 0007
                                Code.Make(Code.OpJump, 13),
                                // 0010
                                Code.Make(Code.OpConstant, 1),
                                // 0013
                                Code.Make(Code.OpPop),
                                // 0014
                                Code.Make(Code.OpConstant, 2),
                                // 0017
                                Code.Make(Code.OpPop)
                        )
                )
        );
        runCompilerTests(tests);
    }

}
