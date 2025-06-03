package code;

import java.util.Map;

public class SymbolTable {

    public Map<String, Symbol> store;
    public int numDefinitions;
    public SymbolTable outer;

    public static final String GlobalScope = "GLOBAL";
    public static final String LocalScope = "LOCAL";

    public SymbolTable() {
        this.store = new java.util.HashMap<>();
        this.numDefinitions = 0;
        this.outer = null;
    }

    public SymbolTable(SymbolTable outer) {
        this.store = new java.util.HashMap<>();
        this.numDefinitions = 0;
        this.outer = outer;
    }

    public Symbol define(String name) {
        Symbol newSymbol;
        if (outer == null) {
            newSymbol = new Symbol(name, GlobalScope, numDefinitions);

        } else {
            newSymbol = new Symbol(name, LocalScope, numDefinitions);

        }
        store.put(name, newSymbol);
        numDefinitions++;
        return newSymbol;
    }

    public Symbol resolve(String name) {
        Symbol curSymbol = store.get(name);
        if (curSymbol == null) {
            curSymbol = outer.resolve(name);
        }
        return curSymbol;
    }

}
