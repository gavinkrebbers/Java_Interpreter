package EvalObject;

import java.util.LinkedList;
import java.util.List;

public class Builtins {

    public static final EvalObject NULL = new NullObj();
    public static final EvalObject TRUE = new BooleanObj(true);
    public static final EvalObject FALSE = new BooleanObj(false);

    public static final Builtin[] builtins = {
        new Builtin(
        "len",
        args -> {
            if (args.size() != 1) {
                return newError("wrong number of arguments. got=" + args.size() + ", want=1");
            } else if (args.get(0) instanceof StringObj stringObj) {
                return new IntegerObj(stringObj.value.length());
            } else if (args.get(0) instanceof ArrayObj arrayObj) {
                return new IntegerObj(arrayObj.elements.size());
            }
            return newError("argument to `len` not supported, got " + args.get(0).type());
        }
        ),
        new Builtin("print", args -> {
            for (EvalObject obj : args) {
                System.out.println(obj.inspect());
            }
            return NULL;
        }),
        new Builtin("first", args -> {
            if (args.size() != 1) {
                return newError("wrong number of arguments. got=" + args.size() + ", want=1");
            } else if (args.get(0) instanceof ArrayObj arrayObj) {
                if (arrayObj.elements.isEmpty()) {
                    return NULL;
                }
                return arrayObj.elements.get(0);
            }
            return newError("argument to `first` must be ARRAY, got " + args.get(0).type());
        }),
        new Builtin("last", args -> {
            if (args.size() != 1) {
                return newError("wrong number of arguments. got=" + args.size() + ", want=1");
            } else if (args.get(0) instanceof ArrayObj arrayObj) {
                if (arrayObj.elements.isEmpty()) {
                    return NULL;
                }
                return arrayObj.elements.get(arrayObj.elements.size() - 1);
            }
            return newError("argument to `last` must be ARRAY, got " + args.get(0).type());
        }),
        new Builtin("rest", args -> {
            if (args.size() != 1) {
                return newError("wrong number of arguments. got=" + args.size() + ", want=1");
            } else if (args.get(0) instanceof ArrayObj arrayObj) {
                ArrayObj outArr = new ArrayObj();
                for (int i = 1; i < arrayObj.elements.size(); i++) {
                    outArr.elements.add(arrayObj.elements.get(i));
                }
                if (outArr.elements.isEmpty()) {
                    return NULL;
                }
                return outArr;
            }
            return newError("argument to `rest` must be ARRAY, got " + args.get(0).type());
        }),
        new Builtin("push", args -> {
            if (args.size() != 2) {
                return newError("wrong number of arguments. got=" + args.size() + ", want=2");
            } else if (!(args.get(0) instanceof ArrayObj)) {
                return newError("argument to `push` must be ARRAY, got " + args.get(0).type());
            }
            List<EvalObject> outputElements = new LinkedList<>();
            for (EvalObject el : ((ArrayObj) args.get(0)).elements) {
                outputElements.add(el);
            }
            outputElements.add(args.get(1));
            ArrayObj arr = new ArrayObj(outputElements);
            return arr;
        }),};

    public static ErrorObj newError(String format, Object... args) {
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append(format);
        for (Object arg : args) {
            errorMessage.append(" ").append(arg.toString());
        }
        return new ErrorObj(errorMessage.toString());
    }

    public static Builtin getBuiltinByName(String name) {

        for (Builtin builtin : builtins) {
            if (builtin.name.equals(name)) {
                return builtin;
            }
        }
        return null;
    }
}
