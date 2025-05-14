package EvalObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Builtins {

    public static final EvalObject NULL = new NullObj();
    public static final EvalObject TRUE = new BooleanObj(true);
    public static final EvalObject FALSE = new BooleanObj(false);

    // private static EvalObject newError(String format, EvalObject... args) {
    //     return new Error(String.format(format, args));
    // }
    public static final Map<String, EvalObject> builtins = new java.util.HashMap<>();

    static {
        {
            builtins.put("len", new Builtin(args -> {
                if (args.size() != 1) {
                    return newError("function `len` expects 1 argument. got ", args.size());
                } else if (args.get(0) instanceof StringObj stringObj) {
                    return new IntegerObj(stringObj.value.length());

                } else if (args.get(0) instanceof ArrayObj arrayObj) {
                    return new IntegerObj(arrayObj.elements.size());

                }
                return newError("function `len` expects STRING input. got", args.get(0).type());

            }));
            builtins.put("first", new Builtin(args -> {
                if (args.size() != 1) {
                    return newError("function `first` expects 1 argument. got ", args.size());
                } else if (args.get(0) instanceof ArrayObj arrayObj) {
                    return arrayObj.elements.get(0);

                }
                return newError("function `first` expects ARRAY input. got", args.get(0).type());

            }));
            builtins.put("rest", new Builtin(args -> {
                if (args.size() != 1) {
                    return newError("function `first` expects 1 argument. got ", args.size());
                } else if (args.get(0) instanceof ArrayObj arrayObj) {
                    ArrayObj outArr = new ArrayObj();
                    for (int i = 1; i < arrayObj.elements.size(); i++) {
                        outArr.elements.add(arrayObj.elements.get(i));
                    }
                    return outArr;

                }
                return newError("function `rest` expects ARRAY input. got", args.get(0).type());

            }));

            builtins.put("push", new Builtin(args -> {
                if (args.size() != 2) {
                    return newError("function `first` expects 1 argument. got ", args.size());
                } else if (!(args.get(0) instanceof ArrayObj)) {
                    return newError("function `len` expects ARRAY, OBJECT input. got", args.get(0).type() + ", " + args.get(1).type());

                }
                List<EvalObject> outputElements = new LinkedList<>();
                for (EvalObject el : ((ArrayObj) args.get(0)).elements) {
                    outputElements.add(el);
                }
                outputElements.add(args.get(1));
                ArrayObj arr = new ArrayObj(outputElements);
                return arr;
            }));

        }
    }

    ;

public static ErrorObj newError(String format, Object... args) {
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append(format);
        for (Object arg : args) {
            errorMessage.append(" ").append(arg.toString());
        }
        return new ErrorObj(errorMessage.toString());
    }
}
