package EvalObject;

public class Builtins {

    public static final EvalObject NULL = new NullObj();
    public static final EvalObject TRUE = new BooleanObj(true);
    public static final EvalObject FALSE = new BooleanObj(false);

    public static final Builtin[] builtins = {
        new Builtin(
        "len",
        args -> {
            if (args.size() != 1) {
                return newError("function `len` expects 1 argument. got ", args.size());
            } else if (args.get(0) instanceof StringObj stringObj) {
                return new IntegerObj(stringObj.value.length());

            } else if (args.get(0) instanceof ArrayObj arrayObj) {
                return new IntegerObj(arrayObj.elements.size());

            }
            return newError("function `len` expects STRING input. got", args.get(0).type());

        }
        ),};

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
