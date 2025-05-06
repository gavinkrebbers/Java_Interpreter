
public class Main {

    public static void main(String[] args) {
        String input = "let";

        Lexer lexer = new Lexer(input);
        Token tok = lexer.nextToken();
        System.out.println(tok.type.TokenType);
        System.out.println("hello");
        System.out.println(Character.isLetter(';'));
    }
}
