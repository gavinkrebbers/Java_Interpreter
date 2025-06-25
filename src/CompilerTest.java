
import Compiler.Bytecode;
import Compiler.CompilationScope;
import Compiler.Compiler;
import Compiler.CompilerError;
import Compiler.EmittedInstruction;
import EvalObject.CompiledFunction;
import EvalObject.EvalObject;
import EvalObject.IntegerObj;
import EvalObject.StringObj;
import Lexer.Lexer;
import Parser.Parser;
import ast.Program;
import code.Code;
import code.Instructions;
import code.Opcode;
import code.SymbolTable;

import org.junit.Test;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
                testInstructions(tt.expectedInstructions, bytecode.instructions);
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
            if (constant instanceof Integer) {
                testIntegerObject(((Integer) constant).longValue(), actual.get(i));
            } else if (constant instanceof Long) {
                testIntegerObject((Long) constant, actual.get(i));
            } else if (constant instanceof String) {
                testStringObject((String) constant, actual.get(i));
            } else if (constant instanceof List) {
                @SuppressWarnings("unchecked")
                List<byte[]> expectedInstructions = (List<byte[]>) constant;
                if (!(actual.get(i) instanceof CompiledFunction)) {
                    throw new Exception("constant is not a compiled function");
                }
                CompiledFunction compiledFn = (CompiledFunction) actual.get(i);
                testInstructions(expectedInstructions, compiledFn.instructions);
            } else {
                throw new Exception("unhandled constant type: " + (constant != null ? constant.getClass() : "null"));
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

    private void testStringObject(String expected, EvalObject actual) throws Exception {
        if (!(actual instanceof StringObj)) {
            throw new Exception(String.format(
                    "object is not StringObj. got=%s (%s)",
                    actual == null ? "null" : actual.getClass(),
                    actual
            ));
        }
        StringObj result = (StringObj) actual;
        if (!result.value.equals(expected)) {
            throw new Exception(String.format(
                    "object has wrong value. got=%s, want=%s",
                    result.value,
                    expected
            ));
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

    @Test
    public void testGlobalLetStatements() {
        List<CompilerTestCase> tests = Arrays.asList(
                new CompilerTestCase(
                        "let one = 1; let two = 2;",
                        Arrays.asList(1, 2),
                        Arrays.asList(
                                Code.Make(Code.OpConstant, 0),
                                Code.Make(Code.OpSetGlobal, 0),
                                Code.Make(Code.OpConstant, 1),
                                Code.Make(Code.OpSetGlobal, 1)
                        )
                ),
                new CompilerTestCase(
                        "let one = 1; one;",
                        Arrays.asList(1),
                        Arrays.asList(
                                Code.Make(Code.OpConstant, 0),
                                Code.Make(Code.OpSetGlobal, 0),
                                Code.Make(Code.OpGetGlobal, 0),
                                Code.Make(Code.OpPop)
                        )
                ),
                new CompilerTestCase(
                        "let one = 1; let two = one; two;",
                        Arrays.asList(1),
                        Arrays.asList(
                                Code.Make(Code.OpConstant, 0),
                                Code.Make(Code.OpSetGlobal, 0),
                                Code.Make(Code.OpGetGlobal, 0),
                                Code.Make(Code.OpSetGlobal, 1),
                                Code.Make(Code.OpGetGlobal, 1),
                                Code.Make(Code.OpPop)
                        )
                )
        );
        runCompilerTests(tests);
    }

    @Test
    public void testStringExpressions() {
        List<CompilerTestCase> tests = Arrays.asList(
                new CompilerTestCase(
                        "\"monkey\"",
                        Arrays.asList("monkey"),
                        Arrays.asList(
                                Code.Make(Code.OpConstant, 0),
                                Code.Make(Code.OpPop)
                        )
                ),
                new CompilerTestCase(
                        "\"mon\" + \"key\"",
                        Arrays.asList("mon", "key"),
                        Arrays.asList(
                                Code.Make(Code.OpConstant, 0),
                                Code.Make(Code.OpConstant, 1),
                                Code.Make(Code.OpAdd),
                                Code.Make(Code.OpPop)
                        )
                )
        );
        runCompilerTests(tests);
    }

    @Test
    public void testArrayLiterals() {
        List<CompilerTestCase> tests = Arrays.asList(
                new CompilerTestCase(
                        "[]",
                        Arrays.asList(),
                        Arrays.asList(
                                Code.Make(Code.OpArray, 0),
                                Code.Make(Code.OpPop)
                        )
                ),
                new CompilerTestCase(
                        "[1, 2, 3]",
                        Arrays.asList(1, 2, 3),
                        Arrays.asList(
                                Code.Make(Code.OpConstant, 0),
                                Code.Make(Code.OpConstant, 1),
                                Code.Make(Code.OpConstant, 2),
                                Code.Make(Code.OpArray, 3),
                                Code.Make(Code.OpPop)
                        )
                ),
                new CompilerTestCase(
                        "[1 + 2, 3 - 4, 5 * 6]",
                        Arrays.asList(1, 2, 3, 4, 5, 6),
                        Arrays.asList(
                                Code.Make(Code.OpConstant, 0),
                                Code.Make(Code.OpConstant, 1),
                                Code.Make(Code.OpAdd),
                                Code.Make(Code.OpConstant, 2),
                                Code.Make(Code.OpConstant, 3),
                                Code.Make(Code.OpSub),
                                Code.Make(Code.OpConstant, 4),
                                Code.Make(Code.OpConstant, 5),
                                Code.Make(Code.OpMul),
                                Code.Make(Code.OpArray, 3),
                                Code.Make(Code.OpPop)
                        )
                )
        );
        runCompilerTests(tests);
    }

    @Test
    public void testHashLiterals() {
        List<CompilerTestCase> tests = Arrays.asList(
                new CompilerTestCase(
                        "{}",
                        Arrays.asList(),
                        Arrays.asList(
                                Code.Make(Code.OpHash, 0),
                                Code.Make(Code.OpPop)
                        )
                ),
                new CompilerTestCase(
                        "{1: 2, 3: 4, 5: 6}",
                        Arrays.asList(1, 2, 3, 4, 5, 6),
                        Arrays.asList(
                                Code.Make(Code.OpConstant, 0),
                                Code.Make(Code.OpConstant, 1),
                                Code.Make(Code.OpConstant, 2),
                                Code.Make(Code.OpConstant, 3),
                                Code.Make(Code.OpConstant, 4),
                                Code.Make(Code.OpConstant, 5),
                                Code.Make(Code.OpHash, 6),
                                Code.Make(Code.OpPop)
                        )
                ),
                new CompilerTestCase(
                        "{1: 2 + 3, 4: 5 * 6}",
                        Arrays.asList(1, 2, 3, 4, 5, 6),
                        Arrays.asList(
                                Code.Make(Code.OpConstant, 0),
                                Code.Make(Code.OpConstant, 1),
                                Code.Make(Code.OpConstant, 2),
                                Code.Make(Code.OpAdd),
                                Code.Make(Code.OpConstant, 3),
                                Code.Make(Code.OpConstant, 4),
                                Code.Make(Code.OpConstant, 5),
                                Code.Make(Code.OpMul),
                                Code.Make(Code.OpHash, 4),
                                Code.Make(Code.OpPop)
                        )
                )
        );
        runCompilerTests(tests);
    }

    @Test
    public void testIndexExpressions() {
        List<CompilerTestCase> tests = Arrays.asList(
                new CompilerTestCase(
                        "[1, 2, 3][1 + 1]",
                        Arrays.asList(1, 2, 3, 1, 1),
                        Arrays.asList(
                                Code.Make(Code.OpConstant, 0),
                                Code.Make(Code.OpConstant, 1),
                                Code.Make(Code.OpConstant, 2),
                                Code.Make(Code.OpArray, 3),
                                Code.Make(Code.OpConstant, 3),
                                Code.Make(Code.OpConstant, 4),
                                Code.Make(Code.OpAdd),
                                Code.Make(Code.OpIndex),
                                Code.Make(Code.OpPop)
                        )
                ),
                new CompilerTestCase(
                        "{1: 2}[2 - 1]",
                        Arrays.asList(1, 2, 2, 1),
                        Arrays.asList(
                                Code.Make(Code.OpConstant, 0),
                                Code.Make(Code.OpConstant, 1),
                                Code.Make(Code.OpHash, 2),
                                Code.Make(Code.OpConstant, 2),
                                Code.Make(Code.OpConstant, 3),
                                Code.Make(Code.OpSub),
                                Code.Make(Code.OpIndex),
                                Code.Make(Code.OpPop)
                        )
                )
        );
        runCompilerTests(tests);
    }

    @Test
    public void testCompilerScopes() {
        Compiler compiler = new Compiler();

        // Test initial scope
        assertEquals("scopeIndex wrong", 0, compiler.scopeIndex);

        // Emit instruction in outer scope
        compiler.emit(Code.OpMul);

        // Enter new scope
        compiler.pushScope();
        assertEquals("scopeIndex wrong after enterScope", 1, compiler.scopeIndex);

        // Emit instruction in inner scope
        compiler.emit(Code.OpSub);

        // Verify inner scope instructions
        CompilationScope innerScope = compiler.scopes.get(compiler.scopeIndex);
        assertEquals("instructions length wrong in inner scope",
                1, innerScope.instructions.length);

        EmittedInstruction last = innerScope.lastInstruction;
        assertNotNull("lastInstruction is null", last);
        assertEquals("lastInstruction.Opcode wrong",
                Code.OpSub, last.opcode);

        // Leave inner scope
        compiler.popScope();
        assertEquals("scopeIndex wrong after leaveScope", 0, compiler.scopeIndex);

        // Emit another instruction in outer scope
        compiler.emit(Code.OpAdd);

        // Verify outer scope instructions
        CompilationScope outerScope = compiler.scopes.get(compiler.scopeIndex);
        assertEquals("instructions length wrong in outer scope",
                2, outerScope.instructions.length);

        last = outerScope.lastInstruction;
        assertNotNull("lastInstruction is null", last);
        assertEquals("lastInstruction.Opcode wrong",
                Code.OpAdd, last.opcode);

        EmittedInstruction previous = outerScope.prevInstruction;
        assertNotNull("previousInstruction is null", previous);
        assertEquals("previousInstruction.Opcode wrong",
                Code.OpMul, previous.opcode);
    }

    @Test
    public void testLetStatementScopes() {
        List<CompilerTestCase> tests = Arrays.asList(
                new CompilerTestCase(
                        "let num = 55; fn() { num }",
                        Arrays.asList(
                                55,
                                Arrays.asList(
                                        Code.Make(Code.OpGetGlobal, 0),
                                        Code.Make(Code.OpReturnObject)
                                )
                        ),
                        Arrays.asList(
                                Code.Make(Code.OpConstant, 0),
                                Code.Make(Code.OpSetGlobal, 0),
                                // Code.Make(Code.OpConstant, 1),
                                Code.Make(Code.OpClosure, 1, 0),
                                Code.Make(Code.OpPop)
                        )
                ),
                new CompilerTestCase(
                        "fn() { let num = 55; num }",
                        Arrays.asList(
                                55,
                                Arrays.asList(
                                        Code.Make(Code.OpConstant, 0),
                                        Code.Make(Code.OpSetLocal, 0),
                                        Code.Make(Code.OpGetLocal, 0),
                                        Code.Make(Code.OpReturnObject)
                                )
                        ),
                        Arrays.asList(
                                // Code.Make(Code.OpConstant, 1),
                                Code.Make(Code.OpClosure, 1, 0),
                                Code.Make(Code.OpPop)
                        )
                ),
                new CompilerTestCase(
                        "fn() { let a = 55; let b = 77; a + b }",
                        Arrays.asList(
                                55,
                                77,
                                Arrays.asList(
                                        Code.Make(Code.OpConstant, 0),
                                        Code.Make(Code.OpSetLocal, 0),
                                        Code.Make(Code.OpConstant, 1),
                                        Code.Make(Code.OpSetLocal, 1),
                                        Code.Make(Code.OpGetLocal, 0),
                                        Code.Make(Code.OpGetLocal, 1),
                                        Code.Make(Code.OpAdd),
                                        Code.Make(Code.OpReturnObject)
                                )
                        ),
                        Arrays.asList(
                                // Code.Make(Code.OpConstant, 2),
                                Code.Make(Code.OpClosure, 2, 0),
                                Code.Make(Code.OpPop)
                        )
                )
        );
        runCompilerTests(tests);
    }

    @Test
    public void testFunctions() {
        List<CompilerTestCase> tests = Arrays.asList(
                new CompilerTestCase(
                        "fn() { return 5 + 10 }",
                        Arrays.asList(
                                5,
                                10,
                                Arrays.asList(
                                        Code.Make(Code.OpConstant, 0),
                                        Code.Make(Code.OpConstant, 1),
                                        Code.Make(Code.OpAdd),
                                        Code.Make(Code.OpReturnObject)
                                )
                        ),
                        Arrays.asList(
                                // Code.Make(Code.OpConstant, 2),
                                Code.Make(Code.OpClosure, 2, 0),
                                Code.Make(Code.OpPop)
                        )
                ),
                new CompilerTestCase(
                        "fn() { 5 + 10 }",
                        Arrays.asList(
                                5,
                                10,
                                Arrays.asList(
                                        Code.Make(Code.OpConstant, 0),
                                        Code.Make(Code.OpConstant, 1),
                                        Code.Make(Code.OpAdd),
                                        Code.Make(Code.OpReturnObject)
                                )
                        ),
                        Arrays.asList(
                                // Code.Make(Code.OpConstant, 2),
                                Code.Make(Code.OpClosure, 2, 0),
                                Code.Make(Code.OpPop)
                        )
                ),
                new CompilerTestCase(
                        "fn() { 1; 2 }",
                        Arrays.asList(
                                1,
                                2,
                                Arrays.asList(
                                        Code.Make(Code.OpConstant, 0),
                                        Code.Make(Code.OpPop),
                                        Code.Make(Code.OpConstant, 1),
                                        Code.Make(Code.OpReturnObject)
                                )
                        ),
                        Arrays.asList(
                                // Code.Make(Code.OpConstant, 2),
                                Code.Make(Code.OpClosure, 2, 0),
                                Code.Make(Code.OpPop)
                        )
                ),
                new CompilerTestCase(
                        "fn() { }",
                        Arrays.asList(
                                Arrays.asList(
                                        Code.Make(Code.OpReturn)
                                )
                        ),
                        Arrays.asList(
                                // Code.Make(Code.OpConstant, 0),
                                Code.Make(Code.OpClosure, 0, 0),
                                Code.Make(Code.OpPop)
                        )
                ),
                new CompilerTestCase(
                        "let oneArg = fn(a) {  }; oneArg(1);",
                        Arrays.asList(
                                Arrays.asList(
                                        Code.Make(Code.OpReturn)
                                ),
                                1
                        ),
                        Arrays.asList(
                                // Code.Make(Code.OpConstant, 0),
                                Code.Make(Code.OpClosure, 0, 0),
                                Code.Make(Code.OpSetGlobal, 0),
                                Code.Make(Code.OpGetGlobal, 0),
                                Code.Make(Code.OpConstant, 1),
                                Code.Make(Code.OpCall, 1),
                                Code.Make(Code.OpPop)
                        )
                ),
                new CompilerTestCase(
                        "let manyArg = fn(a, b, c) { }; manyArg(1, 2, 3);",
                        Arrays.asList(
                                Arrays.asList(
                                        Code.Make(Code.OpReturn)
                                ),
                                1,
                                2,
                                3
                        ),
                        Arrays.asList(
                                // Code.Make(Code.OpConstant, 0),
                                Code.Make(Code.OpClosure, 0, 0),
                                Code.Make(Code.OpSetGlobal, 0),
                                Code.Make(Code.OpGetGlobal, 0),
                                Code.Make(Code.OpConstant, 1),
                                Code.Make(Code.OpConstant, 2),
                                Code.Make(Code.OpConstant, 3),
                                Code.Make(Code.OpCall, 3),
                                Code.Make(Code.OpPop)
                        )
                )
        );
        runCompilerTests(tests);
    }

    @Test
    public void testFunctionCalls() {
        List<CompilerTestCase> tests = Arrays.asList(
                new CompilerTestCase(
                        "fn() { 24 }();",
                        Arrays.asList(
                                24,
                                Arrays.asList(
                                        Code.Make(Code.OpConstant, 0),
                                        Code.Make(Code.OpReturnObject)
                                )
                        ),
                        Arrays.asList(
                                // Code.Make(Code.OpConstant, 1),
                                Code.Make(Code.OpClosure, 1, 0),
                                Code.Make(Code.OpCall),
                                Code.Make(Code.OpPop)
                        )
                ),
                new CompilerTestCase(
                        "let noArg = fn() { 24 }; noArg();",
                        Arrays.asList(
                                24,
                                Arrays.asList(
                                        Code.Make(Code.OpConstant, 0),
                                        Code.Make(Code.OpReturnObject)
                                )
                        ),
                        Arrays.asList(
                                // Code.Make(Code.OpConstant, 1),
                                Code.Make(Code.OpClosure, 1, 0),
                                Code.Make(Code.OpSetGlobal, 0),
                                Code.Make(Code.OpGetGlobal, 0),
                                Code.Make(Code.OpCall),
                                Code.Make(Code.OpPop)
                        )
                ),
                new CompilerTestCase(
                        "let oneArg = fn(a) { a }; oneArg(24);",
                        Arrays.asList(
                                Arrays.asList(
                                        Code.Make(Code.OpGetLocal, 0),
                                        Code.Make(Code.OpReturnObject)
                                ),
                                24
                        ),
                        Arrays.asList(
                                // Code.Make(Code.OpConstant, 0),
                                Code.Make(Code.OpClosure, 0, 0),
                                Code.Make(Code.OpSetGlobal, 0),
                                Code.Make(Code.OpGetGlobal, 0),
                                Code.Make(Code.OpConstant, 1),
                                Code.Make(Code.OpCall, 1),
                                Code.Make(Code.OpPop)
                        )
                ),
                new CompilerTestCase(
                        "let manyArg = fn(a, b, c) { a; b; c }; manyArg(24, 25, 26);",
                        Arrays.asList(
                                Arrays.asList(
                                        Code.Make(Code.OpGetLocal, 0),
                                        Code.Make(Code.OpPop),
                                        Code.Make(Code.OpGetLocal, 1),
                                        Code.Make(Code.OpPop),
                                        Code.Make(Code.OpGetLocal, 2),
                                        Code.Make(Code.OpReturnObject)
                                ),
                                24,
                                25,
                                26
                        ),
                        Arrays.asList(
                                // Code.Make(Code.OpConstant, 0),
                                Code.Make(Code.OpClosure, 0, 0),
                                Code.Make(Code.OpSetGlobal, 0),
                                Code.Make(Code.OpGetGlobal, 0),
                                Code.Make(Code.OpConstant, 1),
                                Code.Make(Code.OpConstant, 2),
                                Code.Make(Code.OpConstant, 3),
                                Code.Make(Code.OpCall, 3),
                                Code.Make(Code.OpPop)
                        )
                )
        );
        runCompilerTests(tests);
    }

    @Test
    public void testBuiltins() {
        List<CompilerTestCase> tests = Arrays.asList(
                new CompilerTestCase(
                        "len([]); push([], 1);",
                        Arrays.asList(1),
                        Arrays.asList(
                                Code.Make(Code.OpGetBuiltin, 0),
                                Code.Make(Code.OpArray, 0),
                                Code.Make(Code.OpCall, 1),
                                Code.Make(Code.OpPop),
                                Code.Make(Code.OpGetBuiltin, 5),
                                Code.Make(Code.OpArray, 0),
                                Code.Make(Code.OpConstant, 0),
                                Code.Make(Code.OpCall, 2),
                                Code.Make(Code.OpPop)
                        )
                ),
                new CompilerTestCase(
                        "fn() { len([]) }",
                        Arrays.asList(
                                Arrays.asList(
                                        Code.Make(Code.OpGetBuiltin, 0),
                                        Code.Make(Code.OpArray, 0),
                                        Code.Make(Code.OpCall, 1),
                                        Code.Make(Code.OpReturnObject)
                                )
                        ),
                        Arrays.asList(
                                // Code.Make(Code.OpConstant, 0),
                                Code.Make(Code.OpClosure, 0, 0),
                                Code.Make(Code.OpPop)
                        )
                )
        );
        runCompilerTests(tests);
    }

    @Test
    public void testClosures() {
        List<CompilerTestCase> tests = Arrays.asList(
                new CompilerTestCase(
                        "fn(a) {fn(b) { a + b } }",
                        Arrays.asList(
                                Arrays.asList(
                                        Code.Make(Code.OpGetFree, 0),
                                        Code.Make(Code.OpGetLocal, 0),
                                        Code.Make(Code.OpAdd),
                                        Code.Make(Code.OpReturnObject)
                                ),
                                Arrays.asList(
                                        Code.Make(Code.OpGetLocal, 0),
                                        Code.Make(Code.OpClosure, 0, 1),
                                        Code.Make(Code.OpReturnObject)
                                )
                        ),
                        Arrays.asList(
                                Code.Make(Code.OpClosure, 1, 0),
                                Code.Make(Code.OpPop)
                        )
                ),
                new CompilerTestCase(
                        """
                        let global = 55;
                        fn() {
                          let a = 66;
                          fn() {
                            let b = 77;
                            fn() {
                              let c = 88;
                              global + a + b + c;
                            }
                          }
                        }""",
                        Arrays.asList(
                                55,
                                66,
                                77,
                                88,
                                Arrays.asList(
                                        Code.Make(Code.OpConstant, 3),
                                        Code.Make(Code.OpSetLocal, 0),
                                        Code.Make(Code.OpGetGlobal, 0),
                                        Code.Make(Code.OpGetFree, 0),
                                        Code.Make(Code.OpAdd),
                                        Code.Make(Code.OpGetFree, 1),
                                        Code.Make(Code.OpAdd),
                                        Code.Make(Code.OpGetLocal, 0),
                                        Code.Make(Code.OpAdd),
                                        Code.Make(Code.OpReturnObject)
                                ),
                                Arrays.asList(
                                        Code.Make(Code.OpConstant, 2),
                                        Code.Make(Code.OpSetLocal, 0),
                                        Code.Make(Code.OpGetFree, 0),
                                        Code.Make(Code.OpGetLocal, 0),
                                        Code.Make(Code.OpClosure, 4, 2),
                                        Code.Make(Code.OpReturnObject)
                                ),
                                Arrays.asList(
                                        Code.Make(Code.OpConstant, 1),
                                        Code.Make(Code.OpSetLocal, 0),
                                        Code.Make(Code.OpGetLocal, 0),
                                        Code.Make(Code.OpClosure, 5, 1),
                                        Code.Make(Code.OpReturnObject)
                                )
                        ),
                        Arrays.asList(
                                Code.Make(Code.OpConstant, 0),
                                Code.Make(Code.OpSetGlobal, 0),
                                Code.Make(Code.OpClosure, 6, 0),
                                Code.Make(Code.OpPop)
                        )
                )
        );
        runCompilerTests(tests);
    }

    @Test
    public void testWhileLoopAddition() {
        List<CompilerTestCase> tests = Arrays.asList(
                new CompilerTestCase(
                        """
            let i = 0;
            let sum = 0;
            while (i < 5) {
                let sum = sum + i;
                let i = i + 1;
            }
            """,
                        Arrays.asList(0, 0, 5, 1), // Added constant 1 for i+1 operation
                        Arrays.asList(
                                // let i = 0;
                                Code.Make(Code.OpConstant, 0), // 0: push 0
                                Code.Make(Code.OpSetGlobal, 0), // 3: set global[0] = i

                                // let sum = 0;
                                Code.Make(Code.OpConstant, 1), // 6: push 0
                                Code.Make(Code.OpSetGlobal, 1), // 9: set global[1] = sum

                                // while loop condition:
                                Code.Make(Code.OpConstant, 2), // 12: get i
                                Code.Make(Code.OpGetGlobal, 0), // 15: push 5
                                Code.Make(Code.OpGreaterThan), // 18: i > 5 (since VM only has >)
                                Code.Make(Code.OpJumpNotTruthy, 45), // 19: jump out if !(i < 5)

                                // sum = sum + i;
                                Code.Make(Code.OpGetGlobal, 2), // 22: get sum
                                Code.Make(Code.OpGetGlobal, 0), // 25: get i
                                Code.Make(Code.OpAdd), // 28: sum + i
                                Code.Make(Code.OpSetGlobal, 2), // 29: set sum

                                // i = i + 1;
                                Code.Make(Code.OpGetGlobal, 3), // 32: get i
                                Code.Make(Code.OpConstant, 3), // 35: push 1
                                Code.Make(Code.OpAdd), // 38: i + 1
                                Code.Make(Code.OpSetGlobal, 3), // 39: set i

                                // jump back to while condition
                                Code.Make(Code.OpJump, 12), // 42: jump to while condition

                                // end of loop
                                Code.Make(Code.OpNull) // 45: push null
                        )
                )
        );
        runCompilerTests(tests);
    }
}
