package Parser;

import Lexer.Lexer;
import Token.Token;
import Token.TokenType;
import ast.Identifier;
import ast.LetStatement;
import ast.Program;
import ast.ReturnStatement;
import ast.Statement;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    public Lexer lexer;
    public Token curToken;
    public Token peekToken;
    public List<String> errors;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.errors = new ArrayList<>();
        this.nextToken();
        this.nextToken();

    }

    public Program parseProgram() {
        Program program = new Program();

        while (!curToken.tokenType().equals(TokenType.EOF)) {
            Statement stmt = parseStatement();
            if (stmt != null) {
                program.addStatement(stmt);
            }
            nextToken();
        }

        return program;
    }

    public Statement parseStatement() {
        switch (this.curToken.tokenType()) {
            case TokenType.LET:
                return parseLetStatement();
            case TokenType.RETURN:
                return parseReturnStatement();
            default:
                return null;
        }
    }

    public Statement parseLetStatement() {
        LetStatement stmt = new LetStatement(curToken);
        if (!expectPeek(TokenType.IDENT)) {
            return stmt;
        }
        Identifier ident = new Identifier(curToken, curToken.literal);
        stmt.setIdentifier(ident);
        if (!expectPeek(TokenType.ASSIGN)) {
            return null;
        }

        // TODO implement this 
        while (!curTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }

        return stmt;
    }

    public Statement parseReturnStatement() {
        Statement stmt = new ReturnStatement();
    }

    public void nextToken() {
        curToken = peekToken;
        peekToken = lexer.nextToken();
    }

    public boolean curTokenIs(String tokenType) {
        return tokenType.equals(curToken.tokenType());
    }

    public boolean peekTokenIs(String tokenType) {
        return tokenType.equals(peekToken.tokenType());
    }

    public boolean expectPeek(String tokenType) {
        if (peekTokenIs(tokenType)) {
            nextToken();
            return true;
        }
        peekError(tokenType);
        return false;
    }

    public List<String> errors() {
        return this.errors;
    }

    public void peekError(String tokenType) {
        String msg = "The next token should have been " + tokenType + " but was actually " + peekToken.tokenType();
        errors.add(msg);
    }
}
