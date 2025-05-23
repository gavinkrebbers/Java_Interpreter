package Compiler;

import EvalObject.EvalObject;
import EvalObject.IntegerObj;
import ast.ExpressionStatement;
import ast.InfixExpression;
import ast.IntegerLiteral;
import ast.Program;
import ast.ProgramNode;
import code.Code;
import code.Instructions;
import code.Opcode;
import java.util.ArrayList;
import java.util.List;

public class Compiler {

    public Instructions instructions;
    public List<EvalObject> constants;

    public Compiler() {
        this.instructions = new Instructions(new byte[0]);
        this.constants = new ArrayList<>();
    }

    public Bytecode bytecode() {
        return new Bytecode(this.instructions, this.constants);
    }

    public void compile(ProgramNode node) throws CompilerError {

        if (node instanceof Program program) {

            for (ProgramNode curNode : program.statements) {
                compile(curNode);
            }
        } else if (node instanceof ExpressionStatement expr) {
            compile(expr.expression);
        } else if (node instanceof InfixExpression infixExpression) {
            compile(infixExpression.left);
            compile(infixExpression.right);

        } else if (node instanceof IntegerLiteral integerLiteral) {
            emit(Code.OpConstant, addConstant(new IntegerObj(integerLiteral.value)));
        }

    }

    public int addConstant(EvalObject obj) {
        constants.add(obj);
        // pointer to constant we just added
        return constants.size() - 1;
    }

    public int emit(Opcode op, int... operands) {
        byte[] ins = Code.Make(op, operands);
        int position = addInstruction(ins);
        return position;
    }

    public int addInstruction(byte[] ins) {
        int posNewInstruction = instructions.instructions.length;
        instructions.addInstruction(ins);
        return posNewInstruction;
    }

}
