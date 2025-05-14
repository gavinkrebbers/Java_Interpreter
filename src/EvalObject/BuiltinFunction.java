package EvalObject;

@FunctionalInterface
public interface BuiltinFunction {

    Object apply(Object... args);
}
