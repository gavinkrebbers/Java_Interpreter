package Evaluator;

import EvalObject.BooleanObj;
import EvalObject.Builtin;
import EvalObject.Builtins;
import EvalObject.ErrorObj;
import EvalObject.EvalObject;
import EvalObject.NullObj;
import java.util.Map;

public class BuiltinsEval {

    public static final EvalObject NULL = new NullObj();
    public static final EvalObject TRUE = new BooleanObj(true);
    public static final EvalObject FALSE = new BooleanObj(false);

    public static final Map<String, Builtin> builtins = new java.util.HashMap<>();

    static {
        {
            builtins.put("len", Builtins.getBuiltinByName("len"));
            builtins.put("first", Builtins.getBuiltinByName("first"));
            builtins.put("last", Builtins.getBuiltinByName("last"));
            builtins.put("rest", Builtins.getBuiltinByName("rest"));
            builtins.put("push", Builtins.getBuiltinByName("push"));
            builtins.put("print", Builtins.getBuiltinByName("print"));
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
