package Evaluator;

import EvalObject.ArrayObj;
import EvalObject.BooleanObj;
import EvalObject.Builtin;
import EvalObject.Environment;
import EvalObject.ErrorObj;
import EvalObject.EvalObject;
import EvalObject.FunctionObj;
import EvalObject.HashObj;
import EvalObject.Hashable;
import EvalObject.IntegerObj;
import EvalObject.NullObj;
import EvalObject.ReturnObj;
import EvalObject.StringObj;
import ast.ArrayLiteral;
import ast.BlockStatement;
import ast.Boolean;
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
import ast.ReturnStatement;
import ast.Statement;
import ast.StringLiteral;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Evaluator {

    final BooleanObj TRUE = new BooleanObj(true);
    final BooleanObj FALSE = new BooleanObj(false);
    final NullObj NULL = new NullObj();
    // public Environment Builtins = new Environment();

    public EvalObject eval(ast.ProgramNode node, Environment env) {
        if (node instanceof Program program) {
            return evalProgram(program.statements, env);
        } else if (node instanceof ExpressionStatement expressionStatement) {
            return eval(expressionStatement.expression, env);
        } else if (node instanceof IntegerLiteral integerLiteral) {
            return new IntegerObj(integerLiteral.getValue());
        } else if (node instanceof Boolean booleanLiteral) {
            return booleanLiteral.value ? this.TRUE : this.FALSE;
        } else if (node instanceof PrefixExpression prefixExpression) {
            EvalObject right = eval(prefixExpression.right, env);
            if (isError(right)) {
                return right;
            }
            return evaluatePrefixOperator(prefixExpression, right);
        } else if (node instanceof InfixExpression infixExpression) {
            EvalObject left = eval(infixExpression.left, env);
            if (isError(left)) {
                return left;
            }
            EvalObject right = eval(infixExpression.right, env);
            if (isError(right)) {
                return right;
            }
            String operator = infixExpression.operator;
            return evalInfixExpression(left, operator, right);
        } else if (node instanceof IfExpression ifExpression) {
            return evaluateIfExpression(ifExpression, env);
        } else if (node instanceof BlockStatement blockStatement) {
            return evalStatements(blockStatement.statements, env);
        } else if (node instanceof ReturnStatement returnStatement) {
            EvalObject val = eval(returnStatement.returnValue, env);
            if (isError(val)) {
                return val;
            }
            return new ReturnObj(val);
        } else if (node instanceof LetStatement letStatement) {
            EvalObject value = eval(letStatement.value, env);
            if (isError(value)) {
                return value;
            }
            env.set(letStatement.identifier.value, value);

        } else if (node instanceof Identifier identifier) {
            return evalIdentifier(identifier, env);

        } else if (node instanceof FunctionLiteral functionLiteral) {
            List<Identifier> params = functionLiteral.getParameters();
            BlockStatement body = functionLiteral.getBody();
            return new FunctionObj(params, body, env);
        } else if (node instanceof CallExpression callExpression) {
            EvalObject func = eval(callExpression.function, env);
            if (isError(func)) {
                return func;
            }
            List<EvalObject> args = evalExpressions(callExpression.arguments, env);

            if (args.size() == 1 && isError(args.get(0))) {
                return args.get(1);
            }

            return applyFunction(func, args);

        } else if (node instanceof StringLiteral stringExpression) {
            return new StringObj(stringExpression.value);
        } else if (node instanceof ArrayLiteral arrayLiteral) {
            List<EvalObject> elements = evalExpressions(arrayLiteral.getElements(), env);

            if (elements.size() == 1 && isError(elements.get(0))) {
                return elements.get(0);
            }
            return new ArrayObj(elements);

        } else if (node instanceof IndexExpression indexExpression) {
            EvalObject left = eval(indexExpression.left, env);
            if (isError(left)) {
                return left;
            }
            EvalObject index = eval(indexExpression.index, env);
            if (isError(index)) {
                return index;
            }
            return evalIndexExpression(left, index);
        } else if (node instanceof HashLiteral hash) {
            return evalHashLiteral(hash, env);
        } else {
            return this.NULL;
        }

        return this.NULL;
    }

    public EvalObject evalIdentifier(Identifier ident, Environment env) {
        EvalObject value = env.get(ident.value);
        if (value != null) {
            return value;

        }
        EvalObject builtin = BuiltinsEval.builtins.get(ident.value);
        if (builtin != null) {
            return builtin;
        }
        return newError("identifier not found:", ident.value);

    }

    public EvalObject applyFunction(EvalObject func, List<EvalObject> args) {
        if (!(func instanceof FunctionObj) && !(func instanceof Builtin)) {
            return newError("not a function:", func.type());
        }
        if (func instanceof Builtin builtin) {
            // Call the builtin function with the arguments
            return builtin.fn.apply(args);
        }

        FunctionObj function = (FunctionObj) func;
        Environment extendedEnvironment = extendEnvironment(function, args);
        EvalObject evaluated = eval(function.body, extendedEnvironment);
        return unwrapReturnValue(evaluated);

    }

    public Environment extendEnvironment(FunctionObj function, List<EvalObject> args) {
        Environment extendedEnvironment = Environment.NewEnclosedEnvironment(function.env);

        for (int i = 0; i < args.size(); i++) {
            extendedEnvironment.set(function.parameters.get(i).value, args.get(i));
        }
        return extendedEnvironment;
    }

    public List<EvalObject> evalExpressions(List<Expression> args, Environment env) {
        List<EvalObject> output = new LinkedList<>();

        for (Expression arg : args) {
            EvalObject evaluated = eval(arg, env);
            if (isError(evaluated)) {
                return List.of(evaluated);
            }
            output.add(evaluated);
        }
        return output;
    }

    public EvalObject evalProgram(List<Statement> statements, Environment env) {
        EvalObject result = null;

        for (Statement stmt : statements) {

            result = eval(stmt, env);
            if (result instanceof ReturnObj returnObj) {
                return returnObj.value;
            }
            if (result instanceof ErrorObj errObj) {
                return errObj;
            }
        }

        return result;
    }

    public EvalObject evalStatements(List<Statement> statements, Environment env) {
        EvalObject result = null;

        for (Statement stmt : statements) {

            result = eval(stmt, env);
            if (result != null && (result.type().equals("RETURN_VALUE") || result.type().equals("ERROR"))) {
                return result;
            }
        }

        return result;
    }

    public EvalObject evaluatePrefixOperator(PrefixExpression prefixExpression, EvalObject right) {
        String operator = prefixExpression.operator;
        switch (operator) {
            case "!":
                return evalBangOperatorExpression(right);
            case "-":
                return evalMinusOperatorExpression(right);
            default:
                return newError("unknown operator: ", operator, right.type());
        }
    }

    public EvalObject unwrapReturnValue(EvalObject obj) {
        if (obj instanceof ReturnObj returnObj) {
            return returnObj.value;
        }
        return obj;
    }

    public EvalObject evalBangOperatorExpression(EvalObject right) {
        if (right == this.TRUE) {
            return this.FALSE;
        } else if (right == this.FALSE) {
            return this.TRUE;
        } else {
            return this.FALSE;
        }
    }

    public EvalObject evalMinusOperatorExpression(EvalObject right) {
        if (!(right instanceof IntegerObj)) {
            return newError("unknown operator: -", right.type());
        }
        return new IntegerObj(-1 * ((IntegerObj) right).getValue());

    }

    public EvalObject evalInfixExpression(EvalObject left, String operator, EvalObject right) {
        if (left instanceof IntegerObj leftInt && right instanceof IntegerObj rightInt) {
            return evalIntegerInfixExpression(leftInt, operator, rightInt);
        } else if (left instanceof StringObj leftString && right instanceof StringObj rightString && operator.equals("+")) {
            return new StringObj(leftString.value + rightString.value);
        } else {
            if (!left.type().equals(right.type())) {
                return newError("type mismatch:", left.type(), operator, right.type());
            }
            switch (operator) {
                case "==":
                    return nativeBoolToBooleanObject(left == right);
                case "!=":
                    return nativeBoolToBooleanObject(left != right);

                default:
                    return newError("unknown operator:", left.type(), operator, right.type());
            }
        }
    }

    public EvalObject evalIntegerInfixExpression(IntegerObj left, String operator, IntegerObj right) {
        switch (operator) {
            case "+":
                return new IntegerObj(left.value + right.value);
            case "-":
                return new IntegerObj(left.value - right.value);
            case "*":
                return new IntegerObj(left.value * right.value);
            case "/":
                return new IntegerObj(left.value / right.value);
            case "<":
                return nativeBoolToBooleanObject(left.value < right.value);
            case ">":
                return nativeBoolToBooleanObject(left.value > right.value);
            case "==":
                return nativeBoolToBooleanObject(left.value == right.value);
            case "!=":
                return nativeBoolToBooleanObject(left.value != right.value);
            default:
                return newError("unknown operator: ", left.type(), operator, right.type());
        }
    }

    public EvalObject evaluateIfExpression(IfExpression ifExpression, Environment env) {
        EvalObject condition = eval(ifExpression.condition, env);
        if (isError(condition)) {
            return condition;
        }
        if (isTruthy(condition)) {
            return eval(ifExpression.consequence, env);
        } else if (ifExpression.alternative != null) {
            return eval(ifExpression.alternative, env);
        }
        return this.NULL;

    }

    public EvalObject evalIndexExpression(EvalObject left, EvalObject index) {
        if (left instanceof ArrayObj arrObj) {
            if (!(index instanceof IntegerObj)) {
                return newError("The type " + index.type() + " is not valid for an index expression");

            }
            IntegerObj integerObj = (IntegerObj) index;
            if (integerObj.value >= arrObj.elements.size()) {
                return newError("Index" + integerObj.value + " is out of bounds for array sized " + arrObj.elements.size());

            }
            return arrObj.elements.get(((IntegerObj) index).value);
        } else if (left instanceof HashObj hash) {
            if (!(index instanceof Hashable)) {
                return newError("Index " + index.type() + " is not valid for an index expression on HASH");
            }
            return hash.atKey(index);
        }
        return newError("index operator not supported: ", left.type());

    }

    public EvalObject evalHashLiteral(HashLiteral hashLiteral, Environment env) {
        HashObj hash = new HashObj();
        for (Map.Entry<Expression, Expression> entry : hashLiteral.pairs.entrySet()) {
            EvalObject key = eval(entry.getKey(), env);
            if (!(key instanceof Hashable)) {
                return null;
            }
            EvalObject value = eval(entry.getValue(), env);
            if (value == null) {
                return null;
            }
            hash.add(key, value);

        }
        return hash;
    }

    public boolean isTruthy(EvalObject obj) {
        if (obj == this.NULL) {
            return false;
        } else if (obj == this.TRUE) {
            return true;
        } else if (obj == this.FALSE) {
            return false;
        } else {
            return true;
        }

    }

    public BooleanObj nativeBoolToBooleanObject(boolean bool) {
        if (bool) {
            return this.TRUE;
        }
        return this.FALSE;
    }

    public ErrorObj newError(String format, Object... args) {
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append(format);
        for (Object arg : args) {
            errorMessage.append(" ").append(arg.toString());
        }
        return new ErrorObj(errorMessage.toString());
    }

    public boolean isError(EvalObject obj) {
        if (obj != null) {
            return obj.type().equals("ERROR");
        }
        return false;
    }
}
