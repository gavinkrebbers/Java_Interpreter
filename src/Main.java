
import Compiler.Compiler;
import Compiler.CompilerError;
import EvalObject.Environment;
import EvalObject.ErrorObj;
import EvalObject.EvalObject;
import Evaluator.Evaluator;
import Lexer.Lexer;
import Parser.Parser;
import ast.Program;
import code.SymbolTable;
import java.util.ArrayList;
import java.util.List;
import vm.ExecutionError;
import vm.VM;

public class Main {

    public static void main(String[] args) {
        // REPL repl = new REPL();
        // repl.start();

        Environment env = new Environment();
        List<EvalObject> globals = new ArrayList<>(VM.GLOBALS_SIZE);
        List<EvalObject> constants = new ArrayList<>();
        SymbolTable symbolTable = new SymbolTable();
        String input = """
    let fibonacci = fn(x) {
        if (x == 0) {
        0
        } else {
            if (x == 1) {
                return 1;
            } else {
                fibonacci(x - 1) + fibonacci(x - 2);
            }
        }
    };
    fibonacci(35);    
        """;
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program p = parser.parseProgram();
        interpret(p, env);
        compile(p, globals);
    }

    public static void interpret(Program program, Environment env) {
        Evaluator e = new Evaluator();
        long start = System.currentTimeMillis();
        EvalObject evaluated = e.eval(program, env);
        if (evaluated instanceof ErrorObj err) {
            System.out.println(err.message);
        } else if (evaluated != null) {
            System.out.println(evaluated.inspect());
            System.out.println("\n");
        }
        long end = System.currentTimeMillis();

        System.out.println("Elapsed time: " + ((end - start) / 1000.0) + " seconds");
    }

    public static void compile(Program program, List<EvalObject> globals) {
        Compiler compiler = new Compiler();

        try {
            compiler.compile(program);

        } catch (CompilerError e) {
            System.out.println("compilation error" + e);
            return;
        }

        VM machine = new VM(compiler.bytecode(), globals);

        try {
            long start = System.currentTimeMillis();

            machine.run();
            long end = System.currentTimeMillis();
            System.out.println("Elapsed time: " + ((end - start) / 1000.0) + " seconds");

        } catch (ExecutionError e) {
            System.out.println("execution error" + e);
            return;
        }
        EvalObject lastPopped = machine.lastPoppedElement();
        System.out.println(lastPopped.inspect());
        System.out.println("\n");
    }

}
