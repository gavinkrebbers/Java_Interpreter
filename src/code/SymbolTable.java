package code;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SymbolTable {

    public Map<String, Symbol> store;
    public List<Symbol> freeSymbols;
    public int numDefinitions;
    public SymbolTable outer;

    public static final String GlobalScope = "GLOBAL";
    public static final String LocalScope = "LOCAL";
    public static final String BuiltinScope = "BUILTIN";
    public static final String FreeScope = "FREE";

    // if the symb is in outer and outer is not global then it is free 
    public SymbolTable() {
        this.store = new java.util.HashMap<>();
        this.numDefinitions = 0;
        this.outer = null;
    }

    public SymbolTable(SymbolTable outer) {
        this.store = new java.util.HashMap<>();
        this.numDefinitions = 0;
        this.outer = outer;
        this.freeSymbols = new ArrayList<>();

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

    public Symbol defineBuiltin(int index, String name) {
        Symbol symbol = new Symbol(name, BuiltinScope, index);
        return store.put(name, symbol);
    }

    public Symbol defineFree(Symbol original) {
        freeSymbols.add(original);

        Symbol symbol = new Symbol(original.name, FreeScope, freeSymbols.size() - 1);
        store.put(original.name, symbol);
        return symbol;
    }

    public Symbol resolve(String name) {
        Symbol curSymbol = store.get(name);
        if (curSymbol == null && outer != null) {
            curSymbol = outer.resolve(name);

            if (curSymbol == null) {
                return null;
            }

            // if is free relative to us
            if (curSymbol.scope.equals(LocalScope)) {
                Symbol freeSymbol = defineFree(curSymbol);
                return freeSymbol;
            }
            return curSymbol;

        }
        return curSymbol;
    }

}
