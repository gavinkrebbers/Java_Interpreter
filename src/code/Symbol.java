package code;

public class Symbol {

    public String name;
    public String scope;
    public int index;

    public Symbol(String name, String scope, int index) {
        this.name = name;
        this.scope = scope;
        this.index = index;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Symbol symbol = (Symbol) obj;
        return index == symbol.index
                && name.equals(symbol.name)
                && scope.equals(symbol.scope);
    }
}
