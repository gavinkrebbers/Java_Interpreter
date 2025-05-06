
import java.util.HashMap;
import java.util.Map;

public class TokenType {

    public String TokenType;

    public static final String ILLEGAL = "ILLEGAL";
    public static final String EOF = "EOF";

    // Identifiers + literals
    public static final String IDENT = "IDENT";
    public static final String INT = "INT";

    // Operators
    public static final String ASSIGN = "=";
    public static final String PLUS = "+";
    public static final String MINUS = "-";
    public static final String BANG = "!";
    public static final String ASTERISK = "*";
    public static final String SLASH = "/";

    public static final String LT = "<";
    public static final String GT = ">";

    public static final String EQ = "==";
    public static final String NOT_EQ = "!=";

    // Delimiters
    public static final String COMMA = ",";
    public static final String SEMICOLON = ";";

    public static final String LPAREN = "(";
    public static final String RPAREN = ")";
    public static final String LBRACE = "{";
    public static final String RBRACE = "}";

    // Keywords
    public static final String FUNCTION = "FUNCTION";
    public static final String LET = "LET";
    public static final String TRUE = "TRUE";
    public static final String FALSE = "FALSE";
    public static final String IF = "IF";
    public static final String ELSE = "ELSE";
    public static final String RETURN = "RETURN";

    public static final Map<String, String> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("fn", FUNCTION);
        keywords.put("let", LET);
        keywords.put("true", TRUE);
        keywords.put("false", FALSE);
        keywords.put("if", IF);
        keywords.put("else", ELSE);
        keywords.put("return", RETURN);
    }

    public TokenType(String TokenType) {
        this.TokenType = TokenType;
    }

}
