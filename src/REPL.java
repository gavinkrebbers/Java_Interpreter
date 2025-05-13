
import EvalObject.Environment;
import EvalObject.ErrorObj;
import EvalObject.EvalObject;
import Evaluator.Evaluator;
import Lexer.Lexer;
import Parser.Parser;
import ast.Program;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class REPL {

    public static void start() {
        Environment env = new Environment();

        String PROMPT = ">>";
        while (true) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                System.out.print(PROMPT);
                String input = reader.readLine();
                // String input = "let fib = fn(n){return fib(n - 1) + fib(n - 2)  }";
                // String input = "let fib = fn(n){if(n==1){return 1} if(n==0){return 1} return fib(n-1) + fib(n-2)  }";
                Lexer l = new Lexer(input);
                Parser p = new Parser(l);
                Program program = p.parseProgram();

                if (!p.errors().isEmpty()) {
                    System.out.println("Parser errors:");
                    for (String error : p.errors()) {
                        System.out.println("\t" + error);
                    }
                    return;
                }

                Evaluator e = new Evaluator();
                EvalObject evaluated = e.eval(program, env);
                if (evaluated instanceof ErrorObj) {
                    System.out.println("there was an error");
                } else if (evaluated != null) {
                    System.out.println(evaluated.inspect());
                    System.out.println("\n");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
