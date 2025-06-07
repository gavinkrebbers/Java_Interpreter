package Compiler;

import EvalObject.BooleanObj;
import EvalObject.Builtins;
import EvalObject.CompiledFunction;
import EvalObject.EvalObject;
import EvalObject.IntegerObj;
import EvalObject.StringObj;
import ast.ArrayLiteral;
import ast.BlockStatement;
import ast.CallExpression;
import ast.Expression;
import ast.ExpressionStatement;
import ast.FunctionLiteral;
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
import ast.ReturnStatement;
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

    // public Instructions instructions;
    public List<EvalObject> constants;
    public SymbolTable symbolTable;

    public List<CompilationScope> scopes;
    public int scopeIndex;

    public static final BooleanObj TRUE = new BooleanObj(true);
    public static final BooleanObj FALSE = new BooleanObj(false);

    public Compiler() {
        this.constants = new ArrayList<>();
        this.symbolTable = new SymbolTable();
        for (int i = 0; i < Builtins.builtins.length; i++) {
            symbolTable.defineBuiltin(i, Builtins.builtins[i].name);
        }
        this.scopes = new ArrayList<>();
        this.scopes.add(new CompilationScope());
        this.scopeIndex = 0;
    }

    public Compiler(SymbolTable s, List<EvalObject> constants) {
        // this.instructions = new Instructions(new byte[0]);
        this.constants = constants;
        this.symbolTable = s;
        this.scopes = new ArrayList<>();
        this.scopes.add(new CompilationScope());

        this.scopeIndex = 0;
    }

    public Bytecode bytecode() {
        return new Bytecode(currentInstructions(), this.constants);
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

            if (lastInstructionIs(Code.OpPop)) {
                removeLastPop();
            }
            int jumpPos = emit(Code.OpJump, 9999);
            changeOperand(jumpNotTruthyPos, currentInstructions().instructions.length);

            if (ifExpression.alternative != null) {
                compile(ifExpression.alternative);
            } else {
                emit(Code.OpNull);
            }

            if (lastInstructionIs(Code.OpPop)) {
                removeLastPop();
            }
            changeOperand(jumpPos, currentInstructions().instructions.length);
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
            emit(symbol.scope.equals(SymbolTable.GlobalScope) ? Code.OpSetGlobal : Code.OpSetLocal, symbol.index);

        } else if (node instanceof Identifier identifier) {
            Symbol symbol = symbolTable.resolve(identifier.value);
            if (symbol == null) {
                throw new CompilerError("Unrecognized identifier: " + identifier.value);
            }
            loadSymbol(symbol);
            // emit(symbol.scope.equals(SymbolTable.GlobalScope) ? Code.OpGetGlobal : Code.OpGetLocal, symbol.index);

        } else if (node instanceof IndexExpression indexExpression) {
            compile(indexExpression.left);
            compile(indexExpression.index);
            emit(Code.OpIndex);
        } else if (node instanceof FunctionLiteral functionLiteral) {
            pushScope();
            for (Expression expr : functionLiteral.parameters) {
                if (expr instanceof Identifier identifier) {
                    Symbol newSymbol = new Symbol(identifier.value, SymbolTable.LocalScope, symbolTable.numDefinitions);
                    symbolTable.define(newSymbol.name);
                }
            }
            compile(functionLiteral.body);
            if (lastInstructionIs(Code.OpPop)) {
                removeLastPop();
                emit(Code.OpReturnObject);
            }
            if (!lastInstructionIs(Code.OpReturnObject)) {
                emit(Code.OpReturn);
            }
            int numLocals = symbolTable.numDefinitions;
            Instructions ins = popScope();
            CompiledFunction compiledFunction = new CompiledFunction(ins, numLocals, functionLiteral.parameters.size());
            emit(Code.OpConstant, addConstant(compiledFunction));
        } else if (node instanceof CallExpression callExpression) {
            compile(callExpression.function);
            for (Expression expr : callExpression.arguments) {
                compile(expr);
            }
            emit(Code.OpCall, callExpression.arguments.size());
        } else if (node instanceof ReturnStatement returnStatement) {
            compile(returnStatement.returnValue);
            emit(Code.OpReturnObject);
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

    public Instructions currentInstructions() {
        return scopes.get(scopeIndex).instructions;
    }

    public int addInstruction(byte[] ins) {
        byte[] prevInstructions = currentInstructions().instructions;

        int posNewInstruction = prevInstructions.length;

        byte[] combined = new byte[prevInstructions.length + ins.length];
        System.arraycopy(prevInstructions, 0, combined, 0, prevInstructions.length);
        System.arraycopy(ins, 0, combined, prevInstructions.length, ins.length);
        Instructions newInstructions = new Instructions(combined);
        scopes.get(scopeIndex).instructions = newInstructions;
        return posNewInstruction;
    }

    public void setLastInstruction(Opcode op, int pos) {
        EmittedInstruction previous = scopes.get(scopeIndex).lastInstruction;
        EmittedInstruction last = new EmittedInstruction(op, pos);
        scopes.get(scopeIndex).prevInstruction = previous;
        scopes.get(scopeIndex).lastInstruction = last;
    }

    public boolean lastInstructionIs(Opcode op) {
        if (currentInstructions().instructions.length == 0) {
            return false;
        }
        return scopes.get(scopeIndex).lastInstruction.opcode.value == op.value;
    }

    public void removeLastPop() {
        int newLength = scopes.get(scopeIndex).lastInstruction.position;
        byte[] truncatedInstruction = new byte[newLength];
        System.arraycopy(currentInstructions().instructions, 0, truncatedInstruction, 0, newLength);
        scopes.get(scopeIndex).instructions = new Instructions(truncatedInstruction);

    }

    public void replaceInstruction(int pos, byte[] newInstruction) {
        for (int i = 0; i < newInstruction.length; i++) {
            currentInstructions().instructions[pos + i] = newInstruction[i];
        }
    }

    public void changeOperand(int opPos, int operand) {
        Opcode newOp = new Opcode(currentInstructions().instructions[opPos]);
        byte[] newInstruction = Code.Make(newOp, operand);
        replaceInstruction(opPos, newInstruction);
    }

    public void pushScope() {
        CompilationScope newScope = new CompilationScope(new Instructions(), new EmittedInstruction(), new EmittedInstruction());
        scopes.add(newScope);
        scopeIndex++;
        symbolTable = new SymbolTable(symbolTable);

    }

    public Instructions popScope() {
        Instructions ins = currentInstructions();
        scopes.remove(scopes.size() - 1);
        scopeIndex--;
        symbolTable = symbolTable.outer;
        return ins;

    }

    public void loadSymbol(Symbol symbol) throws CompilerError {
        switch (symbol.scope) {
            case SymbolTable.LocalScope:
                emit(Code.OpGetLocal, symbol.index);
                break;
            case SymbolTable.GlobalScope:
                emit(Code.OpGetGlobal, symbol.index);
                break;
            case SymbolTable.BuiltinScope:
                emit(Code.OpGetBuiltin, symbol.index);
                break;
            default:
                throw new CompilerError("error in load symbol");
        }
    }
}
