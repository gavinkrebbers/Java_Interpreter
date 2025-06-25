
import Lexer.Lexer;
import Parser.Parser;
import ast.BlockStatement;
import ast.CallExpression;
import ast.Expression;
import ast.ExpressionStatement;
import ast.FunctionLiteral;
import ast.Identifier;
import ast.IfExpression;
import ast.IndexExpression;
import ast.InfixExpression;
import ast.IntegerLiteral;
import ast.LetStatement;
import ast.PrefixExpression;
import ast.Program;
import ast.ReturnStatement;
import ast.Statement;
import ast.WhileStatement;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.ObjDoubleConsumer;

public class ParserTest {

    @Test
    public void testLetStatements() {
        class LetTest {

            String input;
            String expectedIdentifier;
            Object expectedValue;

            LetTest(String input, String expectedIdentifier, Object expectedValue) {
                this.input = input;
                this.expectedIdentifier = expectedIdentifier;
                this.expectedValue = expectedValue;
            }
        }

        LetTest[] tests = {
            new LetTest("let x = 5;", "x", 5),
            new LetTest("let y = true;", "y", true),
            new LetTest("let foobar = y;", "foobar", "y"),};

        for (LetTest tt : tests) {
            Lexer lexer = new Lexer(tt.input);
            Parser parser = new Parser(lexer);
            Program program = parser.parseProgram();
            checkParserErrors(parser);

            List<Statement> statements = program.getStatements();
            assertEquals("program.Statements does not contain 1 statement", 1, statements.size());

            Statement stmt = statements.get(0);
            assertTrue("Statement is not a LetStatement", stmt instanceof LetStatement);

            LetStatement letStmt = (LetStatement) stmt;

            assertEquals("LetStatement name is incorrect", tt.expectedIdentifier, letStmt.identifier.value);
            assertEquals("LetStatement name token literal mismatch", tt.expectedIdentifier, letStmt.identifier.TokenLiteral());

            assertTrue("LetStatement value test failed", testLiteralExpression(letStmt.value, tt.expectedValue));
        }
    }

    private boolean testLetStatement(Statement stmt, String expectedName) {
        assertEquals("Token literal is not 'let'", "let", stmt.TokenLiteral());

        if (!(stmt instanceof LetStatement)) {
            fail("Statement is not a LetStatement. Got: " + stmt.getClass().getSimpleName());
            return false;
        }

        LetStatement letStmt = (LetStatement) stmt;

        assertEquals("letStmt.Name.Value mismatch", expectedName, letStmt.identifier.value);

        return true;
    }

    public static void checkParserErrors(Parser parser) {
        List<String> errors = parser.errors();

        if (errors.isEmpty()) {
            return;
        }

        System.err.printf("Parser has %d errors%n", errors.size());
        for (String msg : errors) {
            System.err.printf("Parser error: \"%s\"%n", msg);
        }

        fail("Parser returned errors");
    }

    @Test
    public void testReturnStatements() {
        String input = """
        return 5;
        return 10;
        return 993322;
        """;

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();

        checkParserErrors(parser);
        assertNotNull("parseProgram() returned null", program);

        List<Statement> statements = program.statements;
        assertEquals("program.Statements does not contain 3 statements", 3, statements.size());

        for (Statement stmt : statements) {
            assertTrue("Statement is not a ReturnStatement", stmt instanceof ReturnStatement);
            ReturnStatement returnStmt = (ReturnStatement) stmt;
            assertEquals("returnStmt.TokenLiteral() is not 'return'", "return", returnStmt.TokenLiteral());
        }
    }

    @Test
    public void testIdentifierExpression() {
        String input = "foobar;";

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();

        checkParserErrors(parser);
        assertNotNull("program is null", program);

        List<Statement> statements = program.statements;
        assertEquals("program should have 1 statement", 1, statements.size());

        Statement stmt = statements.get(0);
        assertTrue("Statement is not ExpressionStatement", stmt instanceof ExpressionStatement);

        ExpressionStatement exprStmt = (ExpressionStatement) stmt;
        assertTrue("Expression is not Identifier", exprStmt.expression instanceof Identifier);

        Identifier ident = (Identifier) exprStmt.expression;
        assertEquals("Identifier value mismatch", "foobar", ident.value);
        assertEquals("Identifier token literal mismatch", "foobar", ident.TokenLiteral());
    }

    private boolean testIntegerLiteral(Expression expr, long expectedValue) {
        assertTrue("Expression is not IntegerLiteral", expr instanceof IntegerLiteral);
        IntegerLiteral literal = (IntegerLiteral) expr;

        if (literal.value != expectedValue) {
            fail("IntegerLiteral value mismatch. Expected: " + expectedValue + ", Got: " + literal.value);
            return false;
        }

        if (!literal.TokenLiteral().equals(Long.toString(expectedValue))) {
            fail("TokenLiteral mismatch. Expected: " + expectedValue + ", Got: " + literal.TokenLiteral());
            return false;
        }

        return true;
    }

    @Test
    public void testParsingPrefixExpressions() {
        class PrefixTest {

            String input;
            String operator;
            Object expectedValue;

            PrefixTest(String input, String operator, Object expectedValue) {
                this.input = input;
                this.operator = operator;
                this.expectedValue = expectedValue;
            }
        }

        PrefixTest[] tests = {
            new PrefixTest("!5;", "!", 5),
            new PrefixTest("-15;", "-", 15),
            new PrefixTest("!true;", "!", true),
            new PrefixTest("!false;", "!", false),};

        for (PrefixTest tt : tests) {
            Lexer lexer = new Lexer(tt.input);
            Parser parser = new Parser(lexer);
            Program program = parser.parseProgram();
            checkParserErrors(parser);

            List<Statement> statements = program.statements;
            assertEquals("program.Statements does not contain 1 statement", 1, statements.size());

            Statement stmt = statements.get(0);
            assertTrue("Statement is not an ExpressionStatement", stmt instanceof ExpressionStatement);

            ExpressionStatement exprStmt = (ExpressionStatement) stmt;
            assertTrue("Expression is not a PrefixExpression", exprStmt.expression instanceof PrefixExpression);

            PrefixExpression exp = (PrefixExpression) exprStmt.expression;
            assertEquals("exp.Operator is incorrect", tt.operator, exp.operator);

            assertTrue("exp.Right literal test failed", testLiteralExpression(exp.right, tt.expectedValue));
        }
    }

    @Test
    public void testParsingInfixExpressions() {
        // Define test cases
        class InfixTest {

            String input;
            long leftValue;
            String operator;
            long rightValue;

            InfixTest(String input, long leftValue, String operator, long rightValue) {
                this.input = input;
                this.leftValue = leftValue;
                this.operator = operator;
                this.rightValue = rightValue;
            }
        }

        InfixTest[] infixTests = {
            new InfixTest("5 + 5;", 5, "+", 5),
            new InfixTest("5 - 5;", 5, "-", 5),
            new InfixTest("5 * 5;", 5, "*", 5),
            new InfixTest("5 / 5;", 5, "/", 5),
            new InfixTest("5 > 5;", 5, ">", 5),
            new InfixTest("5 < 5;", 5, "<", 5),
            new InfixTest("5 == 5;", 5, "==", 5),
            new InfixTest("5 != 5;", 5, "!=", 5),};

        // Loop through each test case
        for (InfixTest tt : infixTests) {
            Lexer lexer = new Lexer(tt.input);
            Parser parser = new Parser(lexer);
            Program program = parser.parseProgram();
            checkParserErrors(parser);

            // Ensure there is exactly 1 statement in the program
            assertEquals("program.Statements does not contain 1 statement", 1, program.statements.size());

            Statement stmt = program.statements.get(0);
            assertTrue("Statement is not an ExpressionStatement", stmt instanceof ExpressionStatement);

            ExpressionStatement exprStmt = (ExpressionStatement) stmt;
            assertTrue("Expression is not an InfixExpression", exprStmt.expression instanceof InfixExpression);

            InfixExpression exp = (InfixExpression) exprStmt.expression;

            // Test left side value
            assertTrue("Left operand is incorrect", testIntegerLiteral(exp.left, tt.leftValue));

            // Test operator
            assertEquals("exp.Operator is incorrect", tt.operator, exp.operator);

            // Test right side value
            assertTrue("Right operand is incorrect", testIntegerLiteral(exp.right, tt.rightValue));
        }
    }

    private boolean testIntegerLiteral(Expression expr, long expectedValue) {
        assertTrue("Expression is not IntegerLiteral", expr instanceof IntegerLiteral);
        IntegerLiteral literal = (IntegerLiteral) expr;
        if (literal.value != expectedValue) {
            fail("IntegerLiteral value mismatch. Expected: " + expectedValue + ", Got: " + literal.value);
            return false;
        }
        return true;
    }

    @Test
    public void testOperatorPrecedenceParsing() {
        class OperatorPrecedenceTest {

            String input;
            String expected;

            OperatorPrecedenceTest(String input, String expected) {
                this.input = input;
                this.expected = expected;
            }
        }

        OperatorPrecedenceTest[] tests = {
            new OperatorPrecedenceTest("-a * b", "((-a) * b)"),
            new OperatorPrecedenceTest("!-a", "(!(-a))"),
            new OperatorPrecedenceTest("a + b + c", "((a + b) + c)"),
            new OperatorPrecedenceTest("a + b - c", "((a + b) - c)"),
            new OperatorPrecedenceTest("a * b * c", "((a * b) * c)"),
            new OperatorPrecedenceTest("a * b / c", "((a * b) / c)"),
            new OperatorPrecedenceTest("a + b / c", "(a + (b / c))"),
            new OperatorPrecedenceTest("3 + 4; -5 * 5", "(3 + 4)((-5) * 5)"),
            new OperatorPrecedenceTest("5 > 4 == 3 < 4", "((5 > 4) == (3 < 4))"),
            new OperatorPrecedenceTest("5 < 4 != 3 > 4", "((5 < 4) != (3 > 4))"),
            new OperatorPrecedenceTest("3 + 4 * 5 == 3 * 1 + 4 * 5", "((3 + (4 * 5)) == ((3 * 1) + (4 * 5)))"),
            new OperatorPrecedenceTest("a + b * c + d / e - f", "(((a + (b * c)) + (d / e)) - f)"),
            new OperatorPrecedenceTest("true", "true"),
            new OperatorPrecedenceTest("false", "false"),
            new OperatorPrecedenceTest("3 > 5 == false", "((3 > 5) == false)"),
            new OperatorPrecedenceTest("3 < 5 == true", "((3 < 5) == true)"),
            new OperatorPrecedenceTest("1 + (2 + 3) + 4", "((1 + (2 + 3)) + 4)"),
            new OperatorPrecedenceTest("(5 + 5) * 2", "((5 + 5) * 2)"),
            new OperatorPrecedenceTest("2 / (5 + 5)", "(2 / (5 + 5))"),
            new OperatorPrecedenceTest("-(5 + 5)", "(-(5 + 5))"),
            new OperatorPrecedenceTest("!(true == true)", "(!(true == true))"),
            new OperatorPrecedenceTest("1 + (2 + 3) + 4", "((1 + (2 + 3)) + 4)"),
            new OperatorPrecedenceTest("(5 + 5) * 2", "((5 + 5) * 2)"),
            new OperatorPrecedenceTest("2 / (5 + 5)", "(2 / (5 + 5))"),
            new OperatorPrecedenceTest("-(5 + 5)", "(-(5 + 5))"),
            new OperatorPrecedenceTest("!(true == true)", "(!(true == true))")
        };

        for (OperatorPrecedenceTest tt : tests) {
            Lexer lexer = new Lexer(tt.input);
            Parser parser = new Parser(lexer);
            Program program = parser.parseProgram();
            checkParserErrors(parser);

            String actual = program.toString();
            assertEquals("Operator precedence test failed for input: " + tt.input,
                    tt.expected, actual);
        }
    }

    private boolean testIdentifier(Expression exp, String expectedValue) {
        assertTrue("Expression is not an Identifier", exp instanceof Identifier);
        Identifier ident = (Identifier) exp;

        if (!ident.value.equals(expectedValue)) {
            fail("Identifier value mismatch. Expected: " + expectedValue + ", Got: " + ident.value);
            return false;
        }

        if (!ident.TokenLiteral().equals(expectedValue)) {
            fail("Identifier TokenLiteral mismatch. Expected: " + expectedValue + ", Got: " + ident.TokenLiteral());
            return false;
        }

        return true;
    }

    private boolean testLiteralExpression(Expression exp, Object expected) {
        if (expected instanceof Integer) {
            return testIntegerLiteral(exp, ((Integer) expected).longValue());
        } else if (expected instanceof Boolean) {
            return testBooleanLiteral(exp, (Boolean) expected);
        } else if (expected instanceof String) {
            return testIdentifier(exp, (String) expected);
        } else {
            fail("Unhandled type in testLiteralExpression: " + expected.getClass().getSimpleName());
            return false;
        }
    }

    @Test
    public void testBooleanExpression() {
        class BooleanTest {

            String input;
            boolean expectedValue;

            BooleanTest(String input, boolean expectedValue) {
                this.input = input;
                this.expectedValue = expectedValue;
            }
        }

        BooleanTest[] tests = {
            new BooleanTest("true;", true),
            new BooleanTest("false;", false),};

        for (BooleanTest tt : tests) {
            Lexer lexer = new Lexer(tt.input);
            Parser parser = new Parser(lexer);
            Program program = parser.parseProgram();
            checkParserErrors(parser);

            List<Statement> statements = program.statements;
            assertEquals("program has not enough statements", 1, statements.size());

            Statement stmt = statements.get(0);
            assertTrue("Statement is not an ExpressionStatement", stmt instanceof ExpressionStatement);

            ExpressionStatement exprStmt = (ExpressionStatement) stmt;
            assertTrue("Expression is not a Boolean", exprStmt.expression instanceof ast.Boolean);

            assertTrue("Boolean literal test failed", testBooleanLiteral(exprStmt.expression, tt.expectedValue));
        }
    }

    private boolean testInfixExpression(Expression exp, Object left, String operator, Object right) {
        assertTrue("Expression is not an InfixExpression", exp instanceof InfixExpression);
        InfixExpression infixExp = (InfixExpression) exp;

        if (!testLiteralExpression(infixExp.left, left)) {
            return false;
        }

        assertEquals("Operator mismatch", operator, infixExp.operator);

        if (!testLiteralExpression(infixExp.right, right)) {
            return false;
        }

        return true;
    }

    private boolean testBooleanLiteral(Expression expr, boolean expectedValue) {
        assertTrue("Expression is not BooleanLiteral", expr instanceof ast.Boolean);
        ast.Boolean boolExpr = (ast.Boolean) expr;

        if (boolExpr.value != expectedValue) {
            fail("Boolean value mismatch. Expected: " + expectedValue + ", Got: " + boolExpr.value);
            return false;
        }

        if (!boolExpr.TokenLiteral().equals(Boolean.toString(expectedValue))) {
            fail("TokenLiteral mismatch. Expected: " + expectedValue + ", Got: " + boolExpr.TokenLiteral());
            return false;
        }

        return true;
    }

    @Test
    public void testIfExpression() {
        class IfExpressionTest {

            String input;

            IfExpressionTest(String input) {
                this.input = input;
            }
        }

        IfExpressionTest[] tests = {
            new IfExpressionTest("if (x < y) { x }")
        };

        for (IfExpressionTest tt : tests) {
            Lexer lexer = new Lexer(tt.input);
            Parser parser = new Parser(lexer);
            Program program = parser.parseProgram();
            checkParserErrors(parser);

            // Check that the program contains one statement
            assertEquals("program.Body does not contain 1 statement", 1, program.statements.size());

            Statement stmt = program.statements.get(0);
            assertTrue("Statement is not an ExpressionStatement", stmt instanceof ExpressionStatement);

            ExpressionStatement exprStmt = (ExpressionStatement) stmt;
            assertTrue("Expression is not an IfExpression", exprStmt.expression instanceof IfExpression);

            IfExpression ifExp = (IfExpression) exprStmt.expression;

            // Test the condition part of the IfExpression
            if (!testInfixExpression(ifExp.getCondition(), "x", "<", "y")) {
                return;
            }

            assertEquals("Consequence is not 1 statement", 1, ifExp.getConsequence().statements.size());
            Statement consequence = ifExp.getConsequence().statements.get(0);
            assertTrue("Consequence is not an ExpressionStatement", consequence instanceof ExpressionStatement);

            ExpressionStatement consequenceStmt = (ExpressionStatement) consequence;
            assertTrue("Consequence expression is not Identifier", testIdentifier(consequenceStmt.expression, "x"));

            assertNull("Alternative should be null", ifExp.getAlternative());
        }
    }

    @Test
    public void testIfElseExpression() {
        String input = "if (x < y) { x } else { y }";

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        checkParserErrors(parser);

        assertEquals("program.Body does not contain 1 statement", 1, program.statements.size());

        Statement stmt = program.statements.get(0);
        assertTrue("Statement is not an ExpressionStatement", stmt instanceof ExpressionStatement);

        ExpressionStatement exprStmt = (ExpressionStatement) stmt;
        assertTrue("Expression is not an IfExpression", exprStmt.expression instanceof IfExpression);

        IfExpression ifExp = (IfExpression) exprStmt.expression;

        if (!testInfixExpression(ifExp.getCondition(), "x", "<", "y")) {
            return;
        }

        assertEquals("Consequence is not 1 statement", 1, ifExp.getConsequence().statements.size());

        Statement consequenceStmt = ifExp.getConsequence().statements.get(0);
        assertTrue("Consequence statement is not ExpressionStatement", consequenceStmt instanceof ExpressionStatement);

        ExpressionStatement consequenceExprStmt = (ExpressionStatement) consequenceStmt;
        assertTrue("Consequence expression is not Identifier", testIdentifier(consequenceExprStmt.expression, "x"));

        assertNotNull("Alternative should not be null", ifExp.getAlternative());
        assertEquals("Alternative does not contain 1 statement", 1, ifExp.getAlternative().statements.size());

        Statement alternativeStmt = ifExp.getAlternative().statements.get(0);
        assertTrue("Alternative statement is not ExpressionStatement", alternativeStmt instanceof ExpressionStatement);

        ExpressionStatement alternativeExprStmt = (ExpressionStatement) alternativeStmt;
        assertTrue("Alternative expression is not Identifier", testIdentifier(alternativeExprStmt.expression, "y"));
    }

    @Test
    public void testFunctionLiteralParsing() {
        String input = "fn(x, y) { x + y; }";

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        checkParserErrors(parser);

        assertEquals("program.Body does not contain 1 statement", 1, program.statements.size());

        Statement stmt = program.statements.get(0);
        assertTrue("Statement is not an ExpressionStatement", stmt instanceof ExpressionStatement);

        ExpressionStatement exprStmt = (ExpressionStatement) stmt;
        assertTrue("Expression is not a FunctionLiteral", exprStmt.expression instanceof FunctionLiteral);

        FunctionLiteral function = (FunctionLiteral) exprStmt.expression;

        assertEquals("Function literal parameters wrong. Expected 2", 2, function.getParameters().size());

        testLiteralExpression(function.getParameters().get(0), "x");
        testLiteralExpression(function.getParameters().get(1), "y");

        assertEquals("Function body does not contain 1 statement", 1, function.getBody().statements.size());

        Statement bodyStmt = function.getBody().statements.get(0);
        assertTrue("Function body statement is not ExpressionStatement", bodyStmt instanceof ExpressionStatement);

        ExpressionStatement bodyExprStmt = (ExpressionStatement) bodyStmt;
        testInfixExpression(bodyExprStmt.expression, "x", "+", "y");
    }

    @Test
    public void testFunctionParameterParsing() {
        class FunctionParameterTest {

            String input;
            String[] expectedParams;

            FunctionParameterTest(String input, String[] expectedParams) {
                this.input = input;
                this.expectedParams = expectedParams;
            }
        }

        FunctionParameterTest[] tests = {
            new FunctionParameterTest("fn() {};", new String[]{}),
            new FunctionParameterTest("fn(x) {};", new String[]{"x"}),
            new FunctionParameterTest("fn(x, y, z) {};", new String[]{"x", "y", "z"})
        };

        for (FunctionParameterTest tt : tests) {
            Lexer lexer = new Lexer(tt.input);
            Parser parser = new Parser(lexer);
            Program program = parser.parseProgram();
            checkParserErrors(parser);

            Statement stmt = program.statements.get(0);
            assertTrue("Statement is not an ExpressionStatement", stmt instanceof ExpressionStatement);

            ExpressionStatement exprStmt = (ExpressionStatement) stmt;
            assertTrue("Expression is not a FunctionLiteral", exprStmt.expression instanceof FunctionLiteral);

            FunctionLiteral function = (FunctionLiteral) exprStmt.expression;
            List<Identifier> params = function.getParameters();

            assertEquals("Incorrect number of parameters", tt.expectedParams.length, params.size());

            for (int i = 0; i < tt.expectedParams.length; i++) {
                testLiteralExpression(params.get(i), tt.expectedParams[i]);
            }
        }
    }

    @Test
    public void testCallExpressionParsing() {
        String input = "add(1, 2 * 3, 4 + 5);";

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        checkParserErrors(parser);

        List<Statement> statements = program.getStatements();
        assertEquals("program.Statements does not contain 1 statement", 1, statements.size());

        Statement stmt = statements.get(0);
        assertTrue("Statement is not an ExpressionStatement", stmt instanceof ExpressionStatement);

        ExpressionStatement exprStmt = (ExpressionStatement) stmt;
        assertTrue("Expression is not a CallExpression", exprStmt.expression instanceof CallExpression);

        CallExpression call = (CallExpression) exprStmt.expression;

        assertTrue("Function is not an Identifier", call.function instanceof Identifier);
        testIdentifier(call.function, "add");

        List<Expression> args = call.arguments;
        assertEquals("Wrong number of arguments", 3, args.size());

        testLiteralExpression(args.get(0), 1);
        testInfixExpression(args.get(1), 2, "*", 3);
        testInfixExpression(args.get(2), 4, "+", 5);
    }

    @Test
    public void testStringLiteralExpression() {
        String input = "\"hello world\";";

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        checkParserErrors(parser);

        List<Statement> statements = program.getStatements();
        assertEquals("program.Statements does not contain 1 statement", 1, statements.size());

        Statement stmt = statements.get(0);
        assertTrue("Statement is not an ExpressionStatement", stmt instanceof ExpressionStatement);

        ExpressionStatement exprStmt = (ExpressionStatement) stmt;
        assertTrue("Expression is not a StringLiteral", exprStmt.expression instanceof ast.StringLiteral);

        ast.StringLiteral literal = (ast.StringLiteral) exprStmt.expression;
        assertEquals("literal.Value not \"hello world\"", "hello world", literal.value);
    }

    @Test
    public void testParsingArrayLiterals() {
        String input = "[1, 2 * 2, 3 + 3]";

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        checkParserErrors(parser);

        List<Statement> statements = program.getStatements();
        assertEquals("program.Statements does not contain 1 statement", 1, statements.size());

        Statement stmt = statements.get(0);
        assertTrue("Statement is not an ExpressionStatement", stmt instanceof ExpressionStatement);

        ExpressionStatement exprStmt = (ExpressionStatement) stmt;
        assertTrue("Expression is not an ArrayLiteral", exprStmt.expression instanceof ast.ArrayLiteral);

        ast.ArrayLiteral array = (ast.ArrayLiteral) exprStmt.expression;
        assertEquals("len(array.Elements) not 3", 3, array.getElements().size());

        // Test first element (integer literal)
        assertTrue("First element test failed", testIntegerLiteral(array.getElements().get(0), 1));

        // Test second element (infix expression)
        assertTrue("Second element test failed",
                testInfixExpression(array.getElements().get(1), 2, "*", 2));

        // Test third element (infix expression)
        assertTrue("Third element test failed",
                testInfixExpression(array.getElements().get(2), 3, "+", 3));
    }

    @Test
    public void testParsingIndexExpressions() {
        String input = "myArray[1 + 1]";

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        checkParserErrors(parser);

        List<Statement> statements = program.getStatements();
        assertEquals("program.Statements does not contain 1 statement", 1, statements.size());

        Statement stmt = statements.get(0);
        assertTrue("Statement is not an ExpressionStatement", stmt instanceof ExpressionStatement);

        ExpressionStatement exprStmt = (ExpressionStatement) stmt;
        assertTrue("Expression is not an IndexExpression",
                exprStmt.expression instanceof IndexExpression);

        IndexExpression indexExp = (IndexExpression) exprStmt.expression;

        // Test the left side (identifier)
        assertTrue("Left expression is not an Identifier",
                testIdentifier(indexExp.left, "myArray"));

        // Test the index expression (infix expression)
        assertTrue("Index expression test failed",
                testInfixExpression(indexExp.index, 1, "+", 1));
    }

    @Test
    public void testParsingHashLiteralsStringKeys() {
        String input = "{\"one\": 1, \"two\": 2, \"three\": 3}";

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        checkParserErrors(parser);

        List<Statement> statements = program.getStatements();
        assertEquals("program.Statements does not contain 1 statement", 1, statements.size());

        Statement stmt = statements.get(0);
        assertTrue("Statement is not an ExpressionStatement", stmt instanceof ExpressionStatement);

        ExpressionStatement exprStmt = (ExpressionStatement) stmt;
        assertTrue("Expression is not a HashLiteral", exprStmt.expression instanceof ast.HashLiteral);

        ast.HashLiteral hash = (ast.HashLiteral) exprStmt.expression;
        assertEquals("hash.Pairs has wrong length", 3, hash.pairs.size());

        Map<String, Integer> expected = new HashMap<>();
        expected.put("one", 1);
        expected.put("two", 2);
        expected.put("three", 3);

        for (Map.Entry<Expression, Expression> entry : hash.pairs.entrySet()) {
            Expression key = entry.getKey();
            assertTrue("key is not a StringLiteral", key instanceof ast.StringLiteral);

            ast.StringLiteral literal = (ast.StringLiteral) key;
            int expectedValue = expected.get(literal.value);
            assertNotNull("Unexpected key in hash: " + literal.value, expectedValue);

            testIntegerLiteral(entry.getValue(), expectedValue);
        }
    }

    @Test
    public void testParsingEmptyHashLiteral() {
        String input = "{}";

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        checkParserErrors(parser);

        List<Statement> statements = program.getStatements();
        assertEquals("program.Statements does not contain 1 statement", 1, statements.size());

        Statement stmt = statements.get(0);
        assertTrue("Statement is not an ExpressionStatement", stmt instanceof ExpressionStatement);

        ExpressionStatement exprStmt = (ExpressionStatement) stmt;
        assertTrue("Expression is not a HashLiteral", exprStmt.expression instanceof ast.HashLiteral);

        ast.HashLiteral hash = (ast.HashLiteral) exprStmt.expression;
        assertTrue("hash.Pairs should be empty", hash.pairs.isEmpty());
    }

    @Test
    public void testParsingHashLiteralsWithExpressions() {
        String input = "{\"one\": 0 + 1, \"two\": 10 - 8, \"three\": 15 / 5}";

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        checkParserErrors(parser);

        List<Statement> statements = program.getStatements();
        assertEquals("program.Statements does not contain 1 statement", 1, statements.size());

        Statement stmt = statements.get(0);
        assertTrue("Statement is not an ExpressionStatement", stmt instanceof ExpressionStatement);

        ExpressionStatement exprStmt = (ExpressionStatement) stmt;
        assertTrue("Expression is not a HashLiteral", exprStmt.expression instanceof ast.HashLiteral);

        ast.HashLiteral hash = (ast.HashLiteral) exprStmt.expression;
        assertEquals("hash.Pairs has wrong length", 3, hash.pairs.size());

        Map<String, Consumer<Expression>> tests = new HashMap<>();
        tests.put("one", e -> testInfixExpression(e, 0, "+", 1));
        tests.put("two", e -> testInfixExpression(e, 10, "-", 8));
        tests.put("three", e -> testInfixExpression(e, 15, "/", 5));

        for (Map.Entry<Expression, Expression> entry : hash.pairs.entrySet()) {
            Expression key = entry.getKey();
            assertTrue("key is not a StringLiteral", key instanceof ast.StringLiteral);

            ast.StringLiteral literal = (ast.StringLiteral) key;
            Consumer<Expression> testFunc = tests.get(literal.value);
            assertNotNull("No test function for key " + literal.value, testFunc);

            testFunc.accept(entry.getValue());
        }
    }

    @Test
    public void testWhileStatement() {
        String input = "while (x < y) { x; }";

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        checkParserErrors(parser);

        List<Statement> statements = program.getStatements();
        assertEquals("program.Statements does not contain 1 statement", 1, statements.size());

        Statement stmt = statements.get(0);
        assertTrue("Statement is not a WhileStatement", stmt instanceof WhileStatement);

        WhileStatement whileStmt = (WhileStatement) stmt;

        // Test the condition
        assertTrue("Condition is not an InfixExpression",
                testInfixExpression(whileStmt.getCondition(), "x", "<", "y"));

        // Test the body
        BlockStatement body = whileStmt.getBody();
        assertEquals("Body should have 1 statement", 1, body.getStatements().size());

        Statement bodyStmt = body.getStatements().get(0);
        assertTrue("Body statement is not an ExpressionStatement",
                bodyStmt instanceof ExpressionStatement);

        ExpressionStatement exprStmt = (ExpressionStatement) bodyStmt;
        assertTrue("Body expression is not an Identifier",
                testIdentifier(exprStmt.expression, "x"));
    }

    @Test
    public void testWhileStatementWithBlock() {
        String input = "while (true) { let x = 5; x; }";

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        checkParserErrors(parser);

        List<Statement> statements = program.getStatements();
        assertEquals("program.Statements does not contain 1 statement", 1, statements.size());

        Statement stmt = statements.get(0);
        assertTrue("Statement is not a WhileStatement", stmt instanceof WhileStatement);

        WhileStatement whileStmt = (WhileStatement) stmt;

        // Test the condition
        assertTrue("Condition is not a Boolean literal",
                testBooleanLiteral(whileStmt.getCondition(), true));

        // Test the body
        BlockStatement body = whileStmt.getBody();
        assertEquals("Body should have 2 statements", 2, body.getStatements().size());

        // Test first statement (let statement)
        Statement firstStmt = body.getStatements().get(0);
        assertTrue("First statement is not a LetStatement",
                firstStmt instanceof LetStatement);

        LetStatement letStmt = (LetStatement) firstStmt;
        assertEquals("LetStatement identifier should be 'x'", "x", letStmt.identifier.value);
        assertTrue("LetStatement value should be 5",
                testIntegerLiteral(letStmt.value, 5));

        // Test second statement (expression statement)
        Statement secondStmt = body.getStatements().get(1);
        assertTrue("Second statement is not an ExpressionStatement",
                secondStmt instanceof ExpressionStatement);

        ExpressionStatement exprStmt = (ExpressionStatement) secondStmt;
        assertTrue("Expression should be an Identifier",
                testIdentifier(exprStmt.expression, "x"));
    }

    @Test
    public void testNestedWhileStatements() {
        String input = "while (x) { while (y) { z; } }";

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        checkParserErrors(parser);

        List<Statement> statements = program.getStatements();
        assertEquals("program.Statements does not contain 1 statement", 1, statements.size());

        Statement stmt = statements.get(0);
        assertTrue("Statement is not a WhileStatement", stmt instanceof WhileStatement);

        WhileStatement outerWhile = (WhileStatement) stmt;

        // Test outer condition
        assertTrue("Outer condition should be an Identifier",
                testIdentifier(outerWhile.getCondition(), "x"));

        // Test outer body (should contain inner while)
        BlockStatement outerBody = outerWhile.getBody();
        assertEquals("Outer body should have 1 statement", 1, outerBody.getStatements().size());

        Statement innerStmt = outerBody.getStatements().get(0);
        assertTrue("Inner statement should be a WhileStatement",
                innerStmt instanceof WhileStatement);

        WhileStatement innerWhile = (WhileStatement) innerStmt;

        // Test inner condition
        assertTrue("Inner condition should be an Identifier",
                testIdentifier(innerWhile.getCondition(), "y"));

        // Test inner body
        BlockStatement innerBody = innerWhile.getBody();
        assertEquals("Inner body should have 1 statement", 1, innerBody.getStatements().size());

        Statement bodyStmt = innerBody.getStatements().get(0);
        assertTrue("Body statement is not an ExpressionStatement",
                bodyStmt instanceof ExpressionStatement);

        ExpressionStatement exprStmt = (ExpressionStatement) bodyStmt;
        assertTrue("Body expression is not an Identifier",
                testIdentifier(exprStmt.expression, "z"));
    }
}
