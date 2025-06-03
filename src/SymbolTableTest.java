
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
        expected.put("c", new Symbol("c", SymbolTable.LocalScope, 0));
        expected.put("d", new Symbol("d", SymbolTable.LocalScope, 1));
        expected.put("e", new Symbol("e", SymbolTable.LocalScope, 0));
        expected.put("f", new Symbol("f", SymbolTable.LocalScope, 1));

        SymbolTable global = new SymbolTable();
        Symbol a = global.define("a");
        assertEquals(expected.get("a"), a);

        Symbol b = global.define("b");
        assertEquals(expected.get("b"), b);

        SymbolTable firstLocal = new SymbolTable(global);
        Symbol c = firstLocal.define("c");
        assertEquals(expected.get("c"), c);

        Symbol d = firstLocal.define("d");
        assertEquals(expected.get("d"), d);

        SymbolTable secondLocal = new SymbolTable(firstLocal);
        Symbol e = secondLocal.define("e");
        assertEquals(expected.get("e"), e);

        Symbol f = secondLocal.define("f");
        assertEquals(expected.get("f"), f);
    }

    @Test
    public void testResolveLocal() {
        // Create global symbol table
        SymbolTable global = new SymbolTable();
        global.define("a");
        global.define("b");

        // Create local symbol table enclosed in global
        SymbolTable local = new SymbolTable(global);
        local.define("c");
        local.define("d");

        // Expected symbols with their scope and index
        Symbol[] expected = {
            new Symbol("a", SymbolTable.GlobalScope, 0),
            new Symbol("b", SymbolTable.GlobalScope, 1),
            new Symbol("c", SymbolTable.LocalScope, 0),
            new Symbol("d", SymbolTable.LocalScope, 1)
        };

        // Verify each symbol resolves correctly
        for (Symbol sym : expected) {
            Symbol result = local.resolve(sym.name);
            assertNotNull("Name " + sym.name + " not resolvable", result);
            assertEquals(sym, result);
        }
    }
}
