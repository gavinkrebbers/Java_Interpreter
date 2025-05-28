package Compiler;

import EvalObject.BooleanObj;
import EvalObject.EvalObject;
import EvalObject.IntegerObj;
import EvalObject.StringObj;
import ast.ArrayLiteral;
import ast.BlockStatement;
import ast.Expression;
import ast.ExpressionStatement;
import ast.HashLiteral;
import ast.Identifier;
import ast.IfExpression;
import ast.IndexExpression;
import ast.InfixExpression;
import ast.IntegerLiteral;
import ast.LetStatement;
import ast.PrefixExpression;
import ast.Program;
import ast.ProgramNode;
import ast.Statement;
import ast.StringLiteral;
import code.Code;
import code.Instructions;
import code.Opcode;
import code.Symbol;
import code.SymbolTable;
import java.util.ArrayList;
import java.util.List;

public class Compiler {

    public Instructions instructions;
    public List<EvalObject> constants;
    public EmittedInstruction lastInstruction;
    public EmittedInstruction prevInstruction;
    public SymbolTable symbolTable;

    public static final BooleanObj TRUE = new BooleanObj(true);
    public static final BooleanObj FALSE = new BooleanObj(false);

    public Compiler() {
        this.instructions = new Instructions(new byte[0]);
        this.constants = new ArrayList<>();
        this.symbolTable = new SymbolTable();
    }

    public Compiler(SymbolTable s, List<EvalObject> constants) {
        this.instructions = new Instructions(new byte[0]);
        this.constants = constants;
        this.symbolTable = s;
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
        } else if (node instanceof StringLiteral stringLiteral) {
            emit(Code.OpConstant, addConstant(new StringObj(stringLiteral.value)));
        } else if (node instanceof ArrayLiteral arrayLiteral) {
            int arrSize = arrayLiteral.elements.size();
            for (int i = 0; i < arrSize; i++) {
                compile(arrayLiteral.elements.get(i));
            }
            emit(Code.OpArray, arrSize);
        } else if (node instanceof HashLiteral hashLiteral) {
            List<Expression> keys = new ArrayList<>();
            hashLiteral.pairs.keySet().forEach((key) -> {
                keys.add(key);
            });
            keys.sort((o1, o2) -> o1.toString().compareTo(o2.toString()));
            for (Expression key : keys) {
                compile(key);
                compile(hashLiteral.pairs.get(key));
            }
            emit(Code.OpHash, keys.size() * 2);

        } else if (node instanceof IfExpression ifExpression) {

            compile(ifExpression.condition);
            int jumpNotTruthyPos = emit(Code.OpJumpNotTruthy, 9999);
            compile(ifExpression.consequence);

            if (lastInstructionIsPop()) {
                removeLastPop();
            }
            int jumpPos = emit(Code.OpJump, 9999);
            changeOperand(jumpNotTruthyPos, instructions.instructions.length);

            if (ifExpression.alternative != null) {
                compile(ifExpression.alternative);
            } else {
                emit(Code.OpNull);
            }

            if (lastInstructionIsPop()) {
                removeLastPop();
            }
            changeOperand(jumpPos, instructions.instructions.length);
        } else if (node instanceof BlockStatement blockStatement) {
            for (Statement stmt : blockStatement.statements) {
                compile(stmt);
            }
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
        } else if (node instanceof LetStatement letStatement) {
            compile(letStatement.value);
            Symbol symbol = symbolTable.define(letStatement.identifier.value);
            emit(Code.OpSetGlobal, symbol.index);
        } else if (node instanceof Identifier identifier) {
            Symbol symbol = symbolTable.resolve(identifier.value);
            if (symbol == null) {
                throw new CompilerError("Unrecognized identifier: " + identifier.value);
            }
            emit(Code.OpGetGlobal, symbol.index);
        } else if (node instanceof IndexExpression indexExpression) {
            compile(indexExpression.left);
            compile(indexExpression.index);
            emit(Code.OpIndex);
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
        setLastInstruction(op, position);
        return position;
    }

    public int addInstruction(byte[] ins) {
        int posNewInstruction = instructions.instructions.length;
        instructions.addInstruction(ins);
        return posNewInstruction;
    }

    public void setLastInstruction(Opcode op, int pos) {
        EmittedInstruction previous = this.lastInstruction;
        EmittedInstruction last = new EmittedInstruction(op, pos);
        this.prevInstruction = previous;
        this.lastInstruction = last;
    }

    public boolean lastInstructionIsPop() {
        return lastInstruction.opcode.value == Code.OpPopValue;
    }

    public void removeLastPop() {
        int newLength = lastInstruction.position;
        byte[] truncated = new byte[newLength];
        System.arraycopy(instructions.instructions, 0, truncated, 0, newLength);
        instructions.instructions = truncated;
        this.lastInstruction = this.prevInstruction;
    }

    public void replaceInstruction(int pos, byte[] newInstruction) {
        for (int i = 0; i < newInstruction.length; i++) {
            instructions.instructions[pos + i] = newInstruction[i];
        }
    }

    public void changeOperand(int opPos, int operand) {
        Opcode newOp = new Opcode(instructions.instructions[opPos]);
        byte[] newInstruction = Code.Make(newOp, operand);
        replaceInstruction(opPos, newInstruction);
    }

}
