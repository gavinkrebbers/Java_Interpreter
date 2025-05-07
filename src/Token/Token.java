package Token;

public class Token {

    public TokenType type;
    public String literal;

    public Token(TokenType type, String literal) {
        this.type = type;
        this.literal = literal;
    }

    public Token() {
        this.type = null;
        this.literal = null;
    }

    public TokenType lookupIdent(String ident) {
        if (TokenType.keywords.get(ident) != null) {
            return new TokenType(TokenType.keywords.get(ident));

        }
        return new TokenType(TokenType.IDENT);
    }

    public String tokenType() {
        return this.type.TokenType;
    }
}
