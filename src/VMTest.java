
import Compiler.Bytecode;
import Compiler.Compiler;
import Compiler.CompilerError;

import org.junit.Test;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import EvalObject.EvalObject;
import EvalObject.IntegerObj;
import Lexer.Lexer;
import Parser.Parser;
import ast.Program;
import vm.ExecutionError;
import vm.VM;

public class VMTest {

    private Program parse(String input) {
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        return parser.parseProgram();
    }

    private void testIntegerObject(long expected, EvalObject actual) throws Exception {
        if (!(actual instanceof IntegerObj)) {
            throw new Exception(String.format("object is not IntegerObj. got=%s (%s)", actual == null ? "null" : actual.getClass(), actual));
        }
        IntegerObj result = (IntegerObj) actual;
        if (result.getValue() != expected) {
            throw new Exception(String.format("object has wrong value. got=%d, want=%d", result.getValue(), expected));
        }
    }

    static class VmTestCase {

        String input;
        Object expected;

        VmTestCase(String input, Object expected) {
            this.input = input;
            this.expected = expected;
        }
    }

    private void runVmTests(VmTestCase[] tests) {
        for (VmTestCase tt : tests) {
            Program program = parse(tt.input);
            Compiler comp = new Compiler();
            try {
                comp.compile(program);

            } catch (CompilerError e) {
                System.out.println("execution error" + e);
                return;
            }
            Bytecode bytecode = comp.bytecode();
            VM vm = new VM(bytecode);
            try {
                vm.run();
            } catch (ExecutionError e) {
                System.out.println("execution error" + e);
                return;
            }

            EvalObject stackElem = vm.stackTop();
            testExpectedObject(tt.expected, stackElem);
        }
    }

    private void testExpectedObject(Object expected, EvalObject actual) {
        if (expected instanceof Integer) {
            try {
                testIntegerObject(((Integer) expected).longValue(), actual);
            } catch (Exception e) {
                fail("testIntegerObject failed: " + e.getMessage());
            }
        } else {
            fail("unhandled expected type: " + (expected == null ? "null" : expected.getClass()));
        }
    }

    @Test
    public void testIntegerArithmetic() {
        VmTestCase[] tests = new VmTestCase[]{
            new VmTestCase("1", 1),
            new VmTestCase("2", 2),
            new VmTestCase("1 + 2", 2)
        };
        runVmTests(tests);
    }
}
