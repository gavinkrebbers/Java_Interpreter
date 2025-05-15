package EvalObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HashObj implements EvalObject {

    public Map<HashKey, Pair> map;

    public HashObj(Map<HashKey, Pair> map) {
        this.map = map;
    }

    public HashObj() {
        this.map = new HashMap<>();
    }

    public void add(EvalObject key, EvalObject value) {
        if (!(key instanceof Hashable) || !(key instanceof Hashable)) {
            return;
        }
        Hashable hashableKey = (Hashable) key;
        map.put(hashableKey.generateHashKey(), new Pair(key, value));

    }

    public EvalObject atKey(EvalObject obj) {
        if (obj instanceof BooleanObj || obj instanceof StringObj || obj instanceof IntegerObj) {
            if (obj instanceof Hashable hashableKey) {
                Pair pair = map.get(hashableKey.generateHashKey());
                if (pair == null) {
                    return new NullObj();

                }
                return pair.value;
            }
            return new NullObj();
        }
        return new NullObj();
    }

    public void setMap(Map<HashKey, Pair> map) {
        this.map = map;
    }

    @Override
    public String type() {
        return "HASH";

    }

    @Override
    public String inspect() {
        StringBuilder output = new StringBuilder();

        List<String> pairList = new LinkedList<>();
        for (Pair pair : map.values()) {
            StringBuilder curPair = new StringBuilder();
            curPair.append(pair.key.inspect());
            curPair.append(":");
            curPair.append(pair.value.inspect());
            pairList.add(curPair.toString());
        }
        output.append("{");
        output.append(String.join(",", pairList));
        output.append("}");
        return output.toString();
    }
}
