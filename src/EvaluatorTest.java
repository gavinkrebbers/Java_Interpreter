
import EvalObject.ArrayObj;
import EvalObject.BooleanObj;
import EvalObject.Environment;
import EvalObject.ErrorObj;
import EvalObject.EvalObject;
import EvalObject.FunctionObj;
import EvalObject.IntegerObj;
import EvalObject.NullObj;
import EvalObject.StringObj;
import Evaluator.Evaluator;
import Lexer.Lexer;
import Parser.Parser;
import ast.Program;
import junit.framework.TestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.function.Function;

import org.junit.Test;
import org.junit.Assert.*;

public class EvaluatorTest {

    @Test
    public void testEvalIntegerExpression() {
        class TestCase {

            String input;
            long expected;

            TestCase(String input, long expected) {
                this.input = input;
                this.expected = expected;
            }
        }

        TestCase[] tests = {
            new TestCase("5", 5),
            new TestCase("10", 10),
            new TestCase("-5", -5),
            new TestCase("-10", -10),};

        for (TestCase tt : tests) {
            EvalObject evaluated = testEval(tt.input);
            assertTrue("Object is not IntegerObject", evaluated instanceof IntegerObj);
            IntegerObj result = (IntegerObj) evaluated;
            assertEquals("Incorrect integer value", tt.expected, result.getValue());
        }
    }

    @Test
    public void testEvalBooleanExpression() {
        class TestCase {

            String input;
            boolean expected;

            TestCase(String input, boolean expected) {
                this.input = input;
                this.expected = expected;
            }
        }

        TestCase[] tests = {
            new TestCase("true", true),
            new TestCase("false", false),
            new TestCase("true == true", true),
            new TestCase("false == false", true),
            new TestCase("true == false", false),
            new TestCase("true != false", true),
            new TestCase("false != true", true),
            new TestCase("(1 < 2) == true", true),
            new TestCase("(1 < 2) == false", false),
            new TestCase("(1 > 2) == true", false),
            new TestCase("(1 > 2) == false", true),};

        for (TestCase tt : tests) {
            EvalObject evaluated = testEval(tt.input);
            assertTrue("Object is not BooleanObj", evaluated instanceof BooleanObj);
            BooleanObj result = (BooleanObj) evaluated;
            assertEquals("Incorrect boolean value", tt.expected, result.getValue());
        }
    }

    @Test
    public void testBangOperator() {
        class TestCase {

            String input;
            boolean expected;

            TestCase(String input, boolean expected) {
                this.input = input;
                this.expected = expected;
            }
        }

        TestCase[] tests = {
            new TestCase("!true", false),
            new TestCase("!false", true),
            new TestCase("!!true", true),
            new TestCase("!!false", false),
            new TestCase("!5", false),
            new TestCase("!!5", true),};

        for (TestCase tt : tests) {
            EvalObject evaluated = testEval(tt.input);
            assertTrue("Object is not BooleanObj", evaluated instanceof BooleanObj);
            BooleanObj result = (BooleanObj) evaluated;
            assertEquals("Incorrect boolean value for input: " + tt.input, tt.expected, result.getValue());
        }
    }

    @Test
    public void testIfElseExpressions() {
        class TestCase {

            String input;
            Object expected;

            TestCase(String input, Object expected) {
                this.input = input;
                this.expected = expected;
            }
        }

        TestCase[] tests = {
            new TestCase("if (true) { 10 }", 10),
            new TestCase("if (false) { 10 }", null),
            new TestCase("if (1) { 10 }", 10),
            new TestCase("if (1 < 2) { 10 }", 10),
            new TestCase("if (1 > 2) { 10 }", null),
            new TestCase("if (1 > 2) { 10 } else { 20 }", 20),
            new TestCase("if (1 < 2) { 10 } else { 20 }", 10)
        };

        for (TestCase tt : tests) {
            EvalObject evaluated = testEval(tt.input);

            if (tt.expected == null) {
                assertTrue("Expected NullObj", evaluated instanceof NullObj);
            } else {
                assertTrue("Expected IntegerObj", evaluated instanceof IntegerObj);
                IntegerObj result = (IntegerObj) evaluated;
                assertEquals("Incorrect integer value for input: " + tt.input, tt.expected, result.getValue());
            }
        }
    }

    @Test
    public void testReturnStatements() {
        class TestCase {

            String input;
            long expected;

            TestCase(String input, long expected) {
                this.input = input;
                this.expected = expected;
            }
        }

        TestCase[] tests = {
            new TestCase("return 10;", 10),
            new TestCase("return 10; 9;", 10),
            new TestCase("return 2 * 5; 9;", 10),
            new TestCase("9; return 2 * 5; 9;", 10),
            new TestCase("if (10 > 1) { if (10 > 1) { return 10; } return 1; }", 10)
        };

        for (TestCase tt : tests) {
            EvalObject evaluated = testEval(tt.input);
            assertTrue("Expected IntegerObj", evaluated instanceof IntegerObj);
            IntegerObj result = (IntegerObj) evaluated;
            assertEquals("Incorrect return value for input: " + tt.input, tt.expected, result.getValue());
        }
    }

    @Test
    public void testErrorHandling() {
        class TestCase {

            String input;
            String expectedMessage;

            TestCase(String input, String expectedMessage) {
                this.input = input;
                this.expectedMessage = expectedMessage;
            }
        }

        TestCase[] tests = {
            new TestCase("5 + true;", "type mismatch: INTEGER + BOOLEAN"),
            new TestCase("5 + true; 5;", "type mismatch: INTEGER + BOOLEAN"),
            new TestCase("-true", "unknown operator: - BOOLEAN"),
            new TestCase("true + false;", "unknown operator: BOOLEAN + BOOLEAN"),
            new TestCase("5; true + false; 5", "unknown operator: BOOLEAN + BOOLEAN"),
            new TestCase("if (10 > 1) { true + false; }", "unknown operator: BOOLEAN + BOOLEAN"),
            new TestCase("""
            if (10 > 1) {
                if (10 > 1) {
                    return true + false;
                }
                return 1;
            }
            """, "unknown operator: BOOLEAN + BOOLEAN"),
            new TestCase("foobar", "identifier not found: foobar"),
            new TestCase("\"Hello\" - \"World\"", "unknown operator: STRING - STRING"),};

        for (TestCase tt : tests) {
            EvalObject evaluated = testEval(tt.input);
            assertTrue("No error object returned for input: " + tt.input, evaluated instanceof ErrorObj);
            ErrorObj errObj = (ErrorObj) evaluated;
            assertEquals("Incorrect error message for input: " + tt.input, tt.expectedMessage, errObj.getMessage());
        }
    }

    @Test
    public void testLetStatements() {
        class TestCase {

            String input;
            long expected;

            TestCase(String input, long expected) {
                this.input = input;
                this.expected = expected;
            }
        }

        TestCase[] tests = {
            new TestCase("let a = 5; a;", 5),
            new TestCase("let a = 5 * 5; a;", 25),
            new TestCase("let a = 5; let b = a; b;", 5),
            new TestCase("let a = 5; let b = a; let c = a + b + 5; c;", 15),};

        for (TestCase tt : tests) {
            EvalObject evaluated = testEval(tt.input);
            assertTrue("Expected IntegerObj", evaluated instanceof IntegerObj);
            IntegerObj result = (IntegerObj) evaluated;
            assertEquals("Incorrect result for input: " + tt.input, tt.expected, result.getValue());
        }
    }

    @Test
    public void testFunctionObject() {
        String input = "fn(x) { x + 2; };";
        EvalObject evaluated = testEval(input);

        assertTrue("Object is not Function", evaluated instanceof FunctionObj);

        FunctionObj fn = (FunctionObj) evaluated;

        assertEquals("Function has wrong number of parameters", 1, fn.getParameters().size());
        assertEquals("Parameter is not 'x'", "x", fn.getParameters().get(0).toString());

        String expectedBody = "(x + 2)";
        assertEquals("Body is not correct", expectedBody, fn.getBody().toString());
    }

    @Test
    public void testFunctionApplication() {
        class TestCase {

            String input;
            long expected;

            TestCase(String input, long expected) {
                this.input = input;
                this.expected = expected;
            }
        }

        TestCase[] tests = {
            new TestCase("let identity = fn(x) { x; }; identity(5);", 5),
            new TestCase("let identity = fn(x) { return x; }; identity(5);", 5),
            new TestCase("let double = fn(x) { x * 2; }; double(5);", 10),
            new TestCase("let add = fn(x, y) { x + y; }; add(5, 5);", 10),
            new TestCase("let add = fn(x, y) { x + y; }; add(5 + 5, add(5, 5));", 20),
            new TestCase("fn(x) { x; }(5)", 5)
        };

        for (TestCase tt : tests) {
            EvalObject evaluated = testEval(tt.input);
            assertTrue("Expected IntegerObj for input: " + tt.input, evaluated instanceof IntegerObj);
            IntegerObj result = (IntegerObj) evaluated;
            assertEquals("Incorrect result for input: " + tt.input, tt.expected, result.getValue());
        }
    }

    @Test
    public void testStringLiteral() {
        String input = "\"Hello World!\"";

        EvalObject evaluated = testEval(input);
        assertTrue("Object is not StringObj", evaluated instanceof StringObj);

        StringObj str = (StringObj) evaluated;
        assertEquals("String has wrong value", "Hello World!", str.value);
    }

    @Test
    public void testArrayLiterals() {
        String input = "[1, 2 * 2, 3 + 3]";

        EvalObject evaluated = testEval(input);
        assertTrue("Object is not ArrayObj", evaluated instanceof ArrayObj);

        ArrayObj result = (ArrayObj) evaluated;
        assertEquals("Array has wrong number of elements", 3, result.getElements().size());

        // Test first element (1)
        assertTrue("First element test failed",
                testIntegerObject(result.getElements().get(0), 1));

        // Test second element (2 * 2 = 4)
        assertTrue("Second element test failed",
                testIntegerObject(result.getElements().get(1), 4));

        // Test third element (3 + 3 = 6)
        assertTrue("Third element test failed",
                testIntegerObject(result.getElements().get(2), 6));
    }

    @Test
    public void testStringConcatenation() {
        class TestCase {

            String input;
            String expected;

            TestCase(String input, String expected) {
                this.input = input;
                this.expected = expected;
            }
        }

        TestCase[] tests = {
            new TestCase("\"\" + \"Hello\"", "Hello"),
            new TestCase("\"Hello\" + \"\"", "Hello"),
            new TestCase("\"\" + \"\"", ""),
            new TestCase("\"A\" + \"B\" + \"C\"", "ABC")
        };

        for (TestCase tt : tests) {
            EvalObject evaluated = testEval(tt.input);
            assertTrue("Object is not StringObj for input: " + tt.input,
                    evaluated instanceof StringObj);
            StringObj str = (StringObj) evaluated;
            assertEquals("String concatenation failed for input: " + tt.input,
                    tt.expected, str.value);
        }
    }

    private boolean testIntegerObject(EvalObject obj, long expected) {
        assertTrue("Object is not IntegerObj", obj instanceof IntegerObj);
        IntegerObj intObj = (IntegerObj) obj;
        if (intObj.getValue() != expected) {
            fail(String.format("Integer value mismatch. Expected %d, got %d",
                    expected, intObj.getValue()));
            return false;
        }
        return true;
    }

    private EvalObject testEval(String input) {
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        Evaluator evaluator = new Evaluator();
        Environment env = new Environment();
        return evaluator.eval(program, env);
    }

}
