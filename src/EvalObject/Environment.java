package EvalObject;

import java.util.HashMap;

public class Environment {

    public HashMap<String, EvalObject> variableStore;
    public Environment outer;

    public Environment() {
        this.variableStore = new HashMap<>();
        this.outer = null;
    }

    public Environment(Environment outer) {
        this.variableStore = new HashMap<>();
        this.outer = outer;
    }

    public Environment newEnclosedEnvironment(Environment outer) {
        return new Environment(outer);
    }

    public EvalObject get(String name) {
        if (!variableStore.containsKey(name)) {
            if (outer == null) {
                return null;
            }
            return outer.get(name);
        }
        return this.variableStore.get(name);

    }

    public EvalObject set(String name, EvalObject value) {

        variableStore.put(name, value);
        return value;

    }

    public static Environment NewEnclosedEnvironment(Environment outer) {
        Environment temp = new Environment();
        temp.outer = outer;

        return temp;
    }
}
