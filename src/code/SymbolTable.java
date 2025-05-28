package code;

import java.util.Map;

public class SymbolTable {

    public Map<String, Symbol> store;
    public int numDefinitions;
    public static final String GlobalScope = "GLOBAL";

    public SymbolTable() {
        this.store = new java.util.HashMap<>();
        this.numDefinitions = 0;
    }

    public Symbol define(String name) {
        Symbol newSymbol = new Symbol(name, GlobalScope, numDefinitions);
        store.put(name, newSymbol);
        numDefinitions++;
        return newSymbol;
    }

    public Symbol resolve(String name) {
        return store.get(name);
    }

}
