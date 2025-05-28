
import code.Code;
import code.Symbol;
import code.SymbolTable;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

public class SymbolTableTest {

    @Test
    public void testDefine() {
        Map<String, Symbol> expected = new HashMap<>();
        expected.put("a", new Symbol("a", SymbolTable.GlobalScope, 0));
        expected.put("b", new Symbol("b", SymbolTable.GlobalScope, 1));

        SymbolTable global = new SymbolTable();
        Symbol a = global.define("a");
        assertEquals(expected.get("a"), a);

        Symbol b = global.define("b");
        assertEquals(expected.get("b"), b);
    }

    @Test
    public void testResolveGlobal() {
        SymbolTable global = new SymbolTable();
        global.define("a");
        global.define("b");

        Symbol[] expected = {
            new Symbol("a", SymbolTable.GlobalScope, 0),
            new Symbol("b", SymbolTable.GlobalScope, 1)
        };

        for (Symbol sym : expected) {
            Symbol result = global.resolve(sym.name);
            assertNotNull("Name " + sym.name + " not resolvable", result);
            assertEquals(sym, result);
        }
    }
}
