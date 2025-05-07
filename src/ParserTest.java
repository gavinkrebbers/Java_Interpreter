
import Lexer.Lexer;
import Parser.Parser;
import ast.LetStatement;
import ast.Program;
import ast.Statement;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

public class ParserTest {

    @Test
    public void testLetStatements() {
        String input = """
            let x = 5;
            let y = 10;
            let foobar =  838383;
            """;

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        checkParserErrors(parser);
        assertNotNull("parseProgram() returned null", program);

        List<Statement> statements = program.statements;
        assertEquals("program.Statements does not contain 3 statements", 3, statements.size());

        String[] expectedIdentifiers = {"x", "y", "foobar"};

        for (int i = 0; i < expectedIdentifiers.length; i++) {
            assertTrue("Statement is not a LetStatement", testLetStatement(statements.get(i), expectedIdentifiers[i]));
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
}
