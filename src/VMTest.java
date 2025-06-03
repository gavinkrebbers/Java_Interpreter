
import Compiler.Bytecode;
import Compiler.Compiler;
import Compiler.CompilerError;
import EvalObject.ArrayObj;
import EvalObject.BooleanObj;

import org.junit.Test;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import EvalObject.EvalObject;
import EvalObject.HashKey;
import EvalObject.HashObj;
import EvalObject.IntegerObj;
import EvalObject.NullObj;
import EvalObject.Pair;
import EvalObject.StringObj;
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
            if (expected instanceof NullObj) {
                if (!(actual instanceof NullObj)) {
                    throw new Exception(String.format("object is not NULL. got=%s", actual));
                }
                return;
            }
            if (expected instanceof Integer) {
                testIntegerObject(((Integer) expected).longValue(), actual);
            } else if (expected instanceof Boolean) {
                testBooleanObject((Boolean) expected, actual);
            } else if (expected instanceof String) {
                testStringObject((String) expected, actual);
            } else if (expected instanceof int[]) {
                testArrayObject((int[]) expected, actual);
            } else if (expected instanceof Map) {
                testHashObject((Map<HashKey, Long>) expected, actual);
            } else {
                fail("unhandled expected type: " + (expected == null ? "null" : expected.getClass()));
            }
        } catch (Exception e) {
            fail("test failed: " + e.getMessage());
        }
    }

    private void testStringObject(String expected, EvalObject actual) throws Exception {
        if (!(actual instanceof StringObj)) {
            throw new Exception(String.format("object is not StringObj. got=%s (%s)",
                    actual == null ? "null" : actual.getClass(),
                    actual));
        }
        StringObj result = (StringObj) actual;
        if (!result.value.equals(expected)) {
            throw new Exception(String.format("object has wrong value. got=%s, want=%s",
                    result.value,
                    expected));
        }
    }

    private void testArrayObject(int[] expected, EvalObject actual) throws Exception {
        if (!(actual instanceof ArrayObj)) {
            throw new Exception(String.format("object is not ArrayObj. got=%s (%s)",
                    actual == null ? "null" : actual.getClass(),
                    actual));
        }

        ArrayObj result = (ArrayObj) actual;
        if (result.getElements().size() != expected.length) {
            throw new Exception(String.format("array has wrong number of elements. got=%d, want=%d",
                    result.getElements().size(), expected.length));
        }

        for (int i = 0; i < expected.length; i++) {
            testIntegerObject(expected[i], result.getElements().get(i));
        }
    }

    private void testHashObject(Map<HashKey, Long> expected, EvalObject actual) throws Exception {
        if (!(actual instanceof HashObj)) {
            throw new Exception(String.format("object is not HashObj. got=%s (%s)",
                    actual == null ? "null" : actual.getClass(),
                    actual));
        }

        HashObj hashObj = (HashObj) actual;
        if (hashObj.map.size() != expected.size()) {
            throw new Exception(String.format("hash has wrong number of pairs. got=%d, want=%d",
                    hashObj.map.size(), expected.size()));
        }

        for (Map.Entry<HashKey, Pair> entry : hashObj.map.entrySet()) {
            HashKey key = entry.getKey();
            EvalObject value = entry.getValue().value;

            if (!expected.containsKey(key)) {
                throw new Exception(String.format("unexpected key in hash: %s", key));
            }

            testIntegerObject(expected.get(key), value);
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
            new VmTestCase("true", true),
            new VmTestCase("false", false),
            new VmTestCase("1 < 2", true),
            new VmTestCase("1 > 2", false),
            new VmTestCase("1 < 1", false),
            new VmTestCase("1 > 1", false),
            new VmTestCase("1 == 1", true),
            new VmTestCase("1 != 1", false),
            new VmTestCase("1 == 2", false),
            new VmTestCase("1 != 2", true),
            new VmTestCase("true == true", true),
            new VmTestCase("false == false", true),
            new VmTestCase("true == false", false),
            new VmTestCase("true != false", true),
            new VmTestCase("false != true", true),
            new VmTestCase("(1 < 2) == true", true),
            new VmTestCase("(1 < 2) == false", false),
            new VmTestCase("(1 > 2) == true", false),
            new VmTestCase("(1 > 2) == false", true),
            new VmTestCase("!true", false),
            new VmTestCase("!false", true),
            new VmTestCase("!5", false),
            new VmTestCase("!!true", true),
            new VmTestCase("!!false", false),
            new VmTestCase("!!5", true),};
        runVmTests(tests);

    }

    @Test
    public void testConditionals() {
        VmTestCase[] tests = new VmTestCase[]{
            new VmTestCase("if (true) { 10 }", 10),
            new VmTestCase("if (true) { 10 } else { 20 }", 10),
            new VmTestCase("if (false) { 10 } else { 20 }", 20),
            new VmTestCase("if (1) { 10 }", 10),
            new VmTestCase("if (1 < 2) { 10 }", 10),
            new VmTestCase("if (1 < 2) { 10 } else { 20 }", 10),
            new VmTestCase("if (1 > 2) { 10 } else { 20 }", 20)
        };
        runVmTests(tests);
    }

    @Test
    public void testGlobalLetStatements() {
        VmTestCase[] tests = new VmTestCase[]{
            new VmTestCase("let one = 1; one", 1),
            new VmTestCase("let one = 1; let two = 2; one + two", 3),
            new VmTestCase("let one = 1; let two = one + one; one + two", 3)
        };
        runVmTests(tests);
    }

    @Test
    public void testStringExpressions() {
        VmTestCase[] tests = new VmTestCase[]{
            new VmTestCase("\"monkey\"", "monkey"),
            new VmTestCase("\"mon\" + \"key\"", "monkey"),
            new VmTestCase("\"mon\" + \"key\" + \"banana\"", "monkeybanana")
        };
        runVmTests(tests);
    }

    @Test
    public void testArrayLiterals() {
        VmTestCase[] tests = new VmTestCase[]{
            new VmTestCase("[]", new int[]{}),
            new VmTestCase("[1, 2, 3]", new int[]{1, 2, 3}),
            new VmTestCase("[1 + 2, 3 * 4, 5 + 6]", new int[]{3, 12, 11})
        };
        runVmTests(tests);
    }

    @Test
    public void testHashLiterals() {
        // Create helper methods to build expected hash maps
        Map<HashKey, Long> emptyMap = new HashMap<>();

        Map<HashKey, Long> simpleMap = new HashMap<>();
        simpleMap.put(new IntegerObj(1).generateHashKey(), 2L);
        simpleMap.put(new IntegerObj(2).generateHashKey(), 3L);

        Map<HashKey, Long> exprMap = new HashMap<>();
        exprMap.put(new IntegerObj(2).generateHashKey(), 4L);
        exprMap.put(new IntegerObj(6).generateHashKey(), 16L);

        VmTestCase[] tests = new VmTestCase[]{
            new VmTestCase("{}", emptyMap),
            new VmTestCase("{1: 2, 2: 3}", simpleMap),
            new VmTestCase("{1 + 1: 2 * 2, 3 + 3: 4 * 4}", exprMap)
        };

        runVmTests(tests);
    }

    @Test
    public void testIndexExpressions() {
        NullObj NULL = new NullObj();
        VmTestCase[] tests = new VmTestCase[]{
            new VmTestCase("[1, 2, 3][1]", 2),
            new VmTestCase("[1, 2, 3][0 + 2]", 3),
            new VmTestCase("[[1, 1, 1]][0][0]", 1),
            new VmTestCase("[][0]", NULL),
            new VmTestCase("[1, 2, 3][99]", VM.NULL_OBJ),
            new VmTestCase("[1][-1]", NULL),
            new VmTestCase("{1: 1, 2: 2}[1]", 1),
            new VmTestCase("{1: 1, 2: 2}[2]", 2),
            new VmTestCase("{1: 1}[0]", NULL),
            new VmTestCase("{}[0]", NULL)
        };
        runVmTests(tests);
    }

    @Test
    public void testFunctionCalls() {
        VmTestCase[] tests = new VmTestCase[]{
            // Existing test case
            new VmTestCase(
            "let fivePlusTen = fn() { 5 + 10; }; fivePlusTen();",
            15
            ),
            new VmTestCase(
            "let one = fn() { 1; };"
            + "let two = fn() { 2; };"
            + "one() + two();",
            3
            ),
            new VmTestCase(
            "let a = fn() { 1 };"
            + "let b = fn() { a() + 1 };"
            + "let c = fn() { b() + 1 };"
            + "c();",
            3
            ),};
        runVmTests(tests);
    }

    @Test
    public void testFunctionReturns() {
        VmTestCase[] tests = new VmTestCase[]{
            new VmTestCase(
            "let earlyExit = fn() { return 99; 100; }; earlyExit();",
            99
            ),
            new VmTestCase(
            "let earlyExit = fn() { return 99; return 100; }; earlyExit();",
            99
            ),};
        runVmTests(tests);
    }

    @Test
    public void testFunctionsWithoutReturnValue() {
        VmTestCase[] tests = new VmTestCase[]{
            new VmTestCase(
            "let noReturn = fn() { }; noReturn();",
            new NullObj()
            ),
            new VmTestCase(
            "let noReturn = fn() { }; "
            + "let noReturnTwo = fn() { noReturn(); }; "
            + "noReturn(); "
            + "noReturnTwo();",
            new NullObj()
            )
        };
        runVmTests(tests);
    }

    @Test
    public void testCallingFunctionsWithBindings() {
        VmTestCase[] tests = new VmTestCase[]{
            new VmTestCase(
            "let one = fn() { let one = 1; one }; one();",
            1
            ),
            new VmTestCase(
            "let oneAndTwo = fn() { let one = 1; let two = 2; one + two; }; oneAndTwo();",
            3
            ),
            new VmTestCase(
            "let oneAndTwo = fn() { let one = 1; let two = 2; one + two; };"
            + "let threeAndFour = fn() { let three = 3; let four = 4; three + four; };"
            + "oneAndTwo() + threeAndFour();",
            10
            ),
            new VmTestCase(
            "let firstFoobar = fn() { let foobar = 50; foobar; };"
            + "let secondFoobar = fn() { let foobar = 100; foobar; };"
            + "firstFoobar() + secondFoobar();",
            150
            ),
            new VmTestCase(
            "let globalSeed = 50;"
            + "let minusOne = fn() { let num = 1; globalSeed - num; };"
            + "let minusTwo = fn() { let num = 2; globalSeed - num; };"
            + "minusOne() + minusTwo();",
            97
            )
        };
        runVmTests(tests);
    }

}
