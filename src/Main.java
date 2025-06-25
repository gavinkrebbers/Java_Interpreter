
public class Main {

    public static void main(String[] args) {
        REPL repl = new REPL();
        repl.start();

        //     Environment env = new Environment();
        //     List<EvalObject> globals = new ArrayList<>(VM.GLOBALS_SIZE);
        //     List<EvalObject> constants = new ArrayList<>();
        //     SymbolTable symbolTable = new SymbolTable();
        //     String input = """
        // let fibonacci = fn(x) {
        //     if (x == 0) {
        //     0
        //     } else {
        //         if (x == 1) {
        //             return 1;
        //         } else {
        //             fibonacci(x - 1) + fibonacci(x - 2);
        //         }
        //     }
        // };
        // fibonacci(35);    
        //     """;
        //     Lexer lexer = new Lexer(input);
        //     Parser parser = new Parser(lexer);
        //     Program program = parser.parseProgram();
        //     Evaluator e = new Evaluator();
        //     long interpStart = System.currentTimeMillis();
        //     EvalObject evaluated = e.eval(program, env);
        //     long interpEnd = System.currentTimeMillis();
        //     double interpTime = (interpEnd - interpStart) / 1000.0;
        //     Compiler compiler = new Compiler();
        //     try {
        //         compiler.compile(program);
        //     } catch (CompilerError ce) {
        //         System.out.println("compilation error" + ce);
        //         return;
        //     }
        //     VM machine = new VM(compiler.bytecode(), globals);
        //     long compStart = System.currentTimeMillis();
        //     try {
        //         machine.run();
        //     } catch (ExecutionError ee) {
        //         System.out.println("Execution error: " + ee.getMessage());
        //         return;
        //     }
        //     long compEnd = System.currentTimeMillis();
        //     EvalObject lastPopped = machine.lastPoppedElement();
        //     System.out.println(lastPopped.inspect());
        //     System.out.println("\n");
        //     double compTime = (compEnd - compStart) / 1000.0;
        //     double speedup = interpTime / compTime;
        //     double percent = ((interpTime - compTime) / interpTime) * 100;
        //     System.out.printf("Compiler is %.2fx faster than interpreter.%n", speedup);
    }

}
