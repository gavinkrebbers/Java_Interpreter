
import Compiler.Bytecode;
import Compiler.Compiler;
import Compiler.CompilerError;
import EvalObject.BooleanObj;

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

    private void testBooleanObject(boolean expected, EvalObject actual) throws Exception {
        if (!(actual instanceof BooleanObj)) {
            throw new Exception(String.format("object is not Boolean. got=%s (%s)",
                    actual == null ? "null" : actual.getClass(), actual));
        }
        BooleanObj result = (BooleanObj) actual;
        if (result.getValue() != expected) {
            throw new Exception(String.format("object has wrong value. got=%b, want=%b",
                    result.getValue(), expected));
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
                fail("CompilerError: " + e.getMessage());
                return;
            }
            Bytecode bytecode = comp.bytecode();
            VM vm = new VM(bytecode);
            try {
                vm.run();
            } catch (ExecutionError e) {
                fail("Execuition Error: " + e.getMessage());
                return;
            }

            EvalObject stackElem = vm.lastPoppedElement();
            testExpectedObject(tt.expected, stackElem);
        }
    }

    private void testExpectedObject(Object expected, EvalObject actual) {
        try {
            if (expected instanceof Integer) {
                testIntegerObject(((Integer) expected).longValue(), actual);
            } else if (expected instanceof Boolean) {
                testBooleanObject((Boolean) expected, actual);
            } else {
                fail("unhandled expected type: " + (expected == null ? "null" : expected.getClass()));
            }
        } catch (Exception e) {
            fail("test failed: " + e.getMessage());
        }
    }

    @Test
    public void testIntegerArithmetic() {
        VmTestCase[] tests = new VmTestCase[]{
            new VmTestCase("1", 1),
            new VmTestCase("2", 2),
            new VmTestCase("1 + 2", 3),
            new VmTestCase("1 - 2", -1),
            new VmTestCase("1 * 2", 2),
            new VmTestCase("4 / 2", 2),
            new VmTestCase("50 / 2 * 2 + 10 - 5", 55),
            new VmTestCase("5 + 5 + 5 + 5 - 10", 10),
            new VmTestCase("2 * 2 * 2 * 2 * 2", 32),
            new VmTestCase("5 * 2 + 10", 20),
            new VmTestCase("5 + 2 * 10", 25),
            new VmTestCase("5 * (2 + 10)", 60),};
        runVmTests(tests);
    }

    @Test
    public void testBooleanExpressions() {
        VmTestCase[] tests = new VmTestCase[]{
            // new VmTestCase("true", true),
            // new VmTestCase("false", false),
            // new VmTestCase("1 < 2", true),
            // new VmTestCase("1 > 2", false),
            // new VmTestCase("1 < 1", false),
            // new VmTestCase("1 > 1", false),
            // new VmTestCase("1 == 1", true),
            // new VmTestCase("1 != 1", false),
            // new VmTestCase("1 == 2", false),
            // new VmTestCase("1 != 2", true),
            // new VmTestCase("true == true", true),
            // new VmTestCase("false == false", true),
            // new VmTestCase("true == false", false),
            // new VmTestCase("true != false", true),
            // new VmTestCase("false != true", true),
            // new VmTestCase("(1 < 2) == true", true),
            // new VmTestCase("(1 < 2) == false", false),
            // new VmTestCase("(1 > 2) == true", false),
            // new VmTestCase("(1 > 2) == false", true),
            new VmTestCase("!true", false),
            new VmTestCase("!false", true), // new VmTestCase("!5", false),
        // new VmTestCase("!!true", true),
        // new VmTestCase("!!false", false),
        // new VmTestCase("!!5", true),
        };
        runVmTests(tests);

    }
}
