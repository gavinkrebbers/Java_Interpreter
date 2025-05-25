package Compiler;

import EvalObject.BooleanObj;
import EvalObject.EvalObject;
import EvalObject.IntegerObj;
import ast.ExpressionStatement;
import ast.InfixExpression;
import ast.IntegerLiteral;
import ast.PrefixExpression;
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

    public static final BooleanObj TRUE = new BooleanObj(true);
    public static final BooleanObj FALSE = new BooleanObj(false);

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
            emit(Code.OpPop);
        } else if (node instanceof InfixExpression infixExpression) {
            if (infixExpression.operator.equals("<")) {
                compile(infixExpression.right);
                compile(infixExpression.left);
                emit(Code.OpGreaterThan);
                return;
            }
            compile(infixExpression.left);
            compile(infixExpression.right);
            String operator = infixExpression.operator;

            switch (operator) {
                case "+":
                    emit(Code.OpAdd);
                    break;
                case "-":
                    emit(Code.OpSub);
                    break;
                case "*":
                    emit(Code.OpMul);
                    break;
                case "/":
                    emit(Code.OpDiv);
                    break;
                case "==":
                    emit(Code.OpEqual);
                    break;
                case "!=":
                    emit(Code.OpNotEqual);
                    break;
                case ">":
                    emit(Code.OpGreaterThan);
                    break;
                default:
                    throw new CompilerError("Unrecognized infix operator : " + operator);
            }

        } else if (node instanceof IntegerLiteral integerLiteral) {
            emit(Code.OpConstant, addConstant(new IntegerObj(integerLiteral.value)));
        } else if (node instanceof ast.Boolean bool) {
            emit(bool.value ? Code.OpTrue : Code.OpFalse);
        } else if (node instanceof PrefixExpression prefixExpression) {
            compile(prefixExpression.right);
            switch (prefixExpression.operator) {
                case "-":
                    emit(Code.OpMinus);
                    break;
                case "!":
                    emit(Code.OpBang);

                    break;
                default:
                    throw new CompilerError("unrecognized prefix expression " + prefixExpression.operator);
            }
        } else {
            throw new CompilerError("Unrecognized operation");
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
