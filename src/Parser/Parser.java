package Parser;

import Lexer.Lexer;
import Token.Token;
import Token.TokenType;
import ast.ArrayLiteral;
import ast.BlockStatement;
import ast.Boolean;
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
import ast.StringLiteral;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class Parser {

    public Lexer lexer;
    public Token curToken;
    public Token peekToken;
    public List<String> errors;
    public HashMap<String, Supplier<Expression>> prefixParseFns;
    public HashMap<String, Function<Expression, Expression>> infixParseFns;
    Map<String, Integer> precedences = new HashMap<>();

    final int LOWEST = 0;
    final int EQUALS = 1;
    final int LESSGREATER = 2;
    final int SUM = 3;
    final int PRODUCT = 4;
    final int PREFIX = 5;
    final int CALL = 6;
    final int INDEX = 7;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.errors = new ArrayList<>();
        this.prefixParseFns = new HashMap<>();
        this.infixParseFns = new HashMap<>();

        nextToken();
        nextToken();
        initializePrecedenceMap();
        initializeInfixFunctions();
        initializePrefixFunctions();

    }

    private void initializePrefixFunctions() {
        registerPrefixFn(TokenType.IDENT, () -> parseIdentifier());
        registerPrefixFn(TokenType.INT, () -> parseIntegerLiteral());
        registerPrefixFn(TokenType.BANG, () -> parsePrefixExpression());
        registerPrefixFn(TokenType.MINUS, () -> parsePrefixExpression());
        registerPrefixFn(TokenType.LPAREN, () -> parseGroupedExpression());
        registerPrefixFn(TokenType.TRUE, () -> parseBoolean());
        registerPrefixFn(TokenType.FALSE, () -> parseBoolean());
        registerPrefixFn(TokenType.IF, () -> parseIfExpression());
        registerPrefixFn(TokenType.STRING, () -> parseStringLiteral());
        registerPrefixFn(TokenType.FUNCTION, () -> parseFunctionLiteral());
        registerPrefixFn(TokenType.LBRACKET, () -> parseArrayLiteral());

    }

    private void initializeInfixFunctions() {
        registerInfixFn(TokenType.PLUS, (left) -> parseInfixExpression(left));
        registerInfixFn(TokenType.MINUS, (left) -> parseInfixExpression(left));
        registerInfixFn(TokenType.SLASH, (left) -> parseInfixExpression(left));
        registerInfixFn(TokenType.ASTERISK, (left) -> parseInfixExpression(left));
        registerInfixFn(TokenType.EQ, (left) -> parseInfixExpression(left));
        registerInfixFn(TokenType.NOT_EQ, (left) -> parseInfixExpression(left));
        registerInfixFn(TokenType.LT, (left) -> parseInfixExpression(left));
        registerInfixFn(TokenType.GT, (left) -> parseInfixExpression(left));
        registerInfixFn(TokenType.LPAREN, (left) -> parseCallExpression(left));
        registerInfixFn(TokenType.LBRACKET, (left) -> parseIndexExpression(left));

    }

    private void initializePrecedenceMap() {
        precedences.put("==", EQUALS);
        precedences.put("!=", EQUALS);
        precedences.put("<", LESSGREATER);
        precedences.put(">", LESSGREATER);
        precedences.put("+", SUM);
        precedences.put("-", SUM);
        precedences.put("/", PRODUCT);
        precedences.put("*", PRODUCT);
        precedences.put("(", CALL);
        precedences.put("[", INDEX);
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
                return parseExpressionStatement();
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
        nextToken();

        stmt.value = parseExpression(LOWEST);
        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }

        return stmt;
    }

    public Statement parseReturnStatement() {
        ReturnStatement stmt = new ReturnStatement(curToken);
        nextToken();
        stmt.returnValue = parseExpression(LOWEST);
        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }
        return stmt;
    }

    public ExpressionStatement parseExpressionStatement() {
        ExpressionStatement stmt = new ExpressionStatement(curToken);

        stmt.expression = parseExpression(LOWEST);

        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }
        return stmt;
    }

    public Expression parseExpression(int precedence) {
        Supplier<Expression> prefixFn = prefixParseFns.get(curToken.tokenType());
        if (prefixFn == null) {
            return null;
        }
        Expression leftExpression = prefixFn.get();
        while (!peekTokenIs(TokenType.SEMICOLON) && precedence < peekPrecedence()) {
            Function<Expression, Expression> infixFn = infixParseFns.get(peekToken.tokenType());
            if (infixFn == null) {
                return leftExpression;
            }
            nextToken();
            leftExpression = infixFn.apply(leftExpression);
        }
        return leftExpression;
    }

    public Identifier parseIdentifier() {

        return new Identifier(curToken, curToken.literal);
    }

    public IntegerLiteral parseIntegerLiteral() {

        try {
            return new IntegerLiteral(curToken, Integer.parseInt(curToken.literal));
        } catch (NumberFormatException e) {
            System.out.println("Failed to parse integer: " + curToken.literal);
            return null;
        }
    }

    public PrefixExpression parsePrefixExpression() {
        PrefixExpression prefixExp = new PrefixExpression(curToken, curToken.literal);

        nextToken();
        Expression right = parseExpression(PREFIX);
        prefixExp.right = right;
        return prefixExp;
    }

    public Expression parseInfixExpression(Expression left) {
        InfixExpression expression = new InfixExpression(curToken, left, curToken.literal);
        int precedence = curPrecedence();
        nextToken();
        expression.right = parseExpression(precedence);
        return expression;
    }

    public Expression parseIfExpression() {
        IfExpression expression = new IfExpression(curToken);
        if (!expectPeek(TokenType.LPAREN)) {
            return null;
        }
        nextToken();

        expression.condition = parseExpression(LOWEST);
        if (!expectPeek(TokenType.RPAREN)) {
            return null;
        }
        if (!expectPeek(TokenType.LBRACE)) {
            return null;
        }
        expression.consequence = parseBlockStatement();
        if (peekTokenIs(TokenType.ELSE)) {
            nextToken();
            if (!expectPeek(TokenType.LBRACE)) {
                return null;
            }
            expression.alternative = parseBlockStatement();
        }

        return expression;
    }

    public BlockStatement parseBlockStatement() {
        BlockStatement block = new BlockStatement(curToken);

        nextToken();
        while (!curTokenIs(TokenType.RBRACE) && !curTokenIs(TokenType.EOF)) {
            Statement stmt = parseStatement();
            if (stmt != null) {
                block.addStatement(stmt);
            }
            nextToken();
        }
        return block;
    }

    public Expression parseGroupedExpression() {
        nextToken();

        Expression exp = parseExpression(LOWEST);

        if (!expectPeek(TokenType.RPAREN)) {
            return null;
        }
        return exp;
    }

    public Expression parseBoolean() {
        return new Boolean(curToken, curTokenIs(TokenType.TRUE));
    }

    public Expression parseFunctionLiteral() {
        FunctionLiteral func = new FunctionLiteral(curToken);

        if (!expectPeek(TokenType.LPAREN)) {
            return null;
        }
        func.parameters = parseFunctionParameters();

        if (!expectPeek(TokenType.LBRACE)) {
            return null;
        }
        func.body = parseBlockStatement();

        return func;
    }

    public List<Identifier> parseFunctionParameters() {
        List<Identifier> output = new ArrayList<Identifier>();
        if (peekTokenIs(TokenType.RPAREN)) {
            nextToken();
            return output;
        }
        nextToken();
        Identifier ident = new Identifier(curToken, curToken.literal);
        output.add(ident);
        while (!peekTokenIs(TokenType.RPAREN)) {

            nextToken();
            nextToken();
            ident = new Identifier(curToken, curToken.literal);
            output.add(ident);
        }

        if (!expectPeek(TokenType.RPAREN)) {
            return null;
        }

        return output;

    }

    public Expression parseCallExpression(Expression function) {
        CallExpression expression = new CallExpression(curToken, function);
        expression.arguments = parseArgumentList(TokenType.RPAREN);
        return expression;
    }

    public Expression parseArrayLiteral() {
        ArrayLiteral arr = new ArrayLiteral(curToken);
        List<Expression> elements = parseArgumentList(TokenType.RBRACKET);
        arr.setElements(elements);
        return arr;
    }

    public List<Expression> parseArgumentList(String end) {
        List<Expression> args = new ArrayList<>();

        if (peekTokenIs(end)) {
            nextToken();
            return args;
        }

        nextToken();
        args.add(parseExpression(LOWEST));
        while (peekTokenIs(TokenType.COMMA)) {
            nextToken();
            nextToken();
            args.add(parseExpression(LOWEST));

        }

        if (!expectPeek(end)) {
            return null;
        }
        return args;
    }

    public Expression parseIndexExpression(Expression left) {
        IndexExpression exp = new IndexExpression(curToken, left);
        nextToken();
        exp.index = parseExpression(LOWEST);
        if (!expectPeek(TokenType.RBRACKET)) {
            return null;
        }
        return exp;
    }

    public Expression parseStringLiteral() {
        return new StringLiteral(curToken, curToken.literal);
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

    public void registerPrefixFn(String tokenType, Supplier<Expression> prefixParseFn) {
        prefixParseFns.put(tokenType, prefixParseFn);
    }

    public void registerInfixFn(String tokenType, Function<Expression, Expression> prefixParseFn) {
        infixParseFns.put(tokenType, prefixParseFn);
    }

    public int peekPrecedence() {
        return precedences.getOrDefault(peekToken.literal, LOWEST);
    }

    public int curPrecedence() {
        return precedences.getOrDefault(curToken.literal, LOWEST);

    }

}
