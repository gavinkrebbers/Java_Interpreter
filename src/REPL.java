
import EvalObject.Environment;
import EvalObject.ErrorObj;
import EvalObject.EvalObject;
import Evaluator.Evaluator;
import Lexer.Lexer;
import Parser.Parser;
import ast.Program;
import code.SymbolTable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import vm.VM;

public class REPL {

    public REPL() {

    }

    public void start() {
        Environment env = new Environment();
        List<EvalObject> globals = new ArrayList<>(VM.GLOBALS_SIZE);
        List<EvalObject> constants = new ArrayList<>();
        SymbolTable symbolTable = new SymbolTable();
        String PROMPT = ">>";
        while (true) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                System.out.print(PROMPT);
                String input = reader.readLine();
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

                Interpret(program, env);
                // Compiler comp = new Compiler(symbolTable, constants);
                // try {
                //     comp.compile(program);
                // } catch (CompilerError e) {
                //     System.out.println("compilation error" + e);
                //     return;
                // }
                // VM machine = new VM(comp.bytecode(), globals);
                // try {
                //     machine.run();
                // } catch (ExecutionError e) {
                //     System.out.println("execution error" + e);
                //     return;
                // }
                // EvalObject lastPopped = machine.lastPoppedElement();
                // System.out.println(lastPopped.inspect());
                // System.out.println("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void Interpret(Program program, Environment env) {
        Evaluator e = new Evaluator();
        EvalObject evaluated = e.eval(program, env);
        if (evaluated instanceof ErrorObj err) {
            System.out.println(err.message);
        } else if (evaluated != null) {
            System.out.println(evaluated.inspect());
            System.out.println("\n");
        }
    }
}
