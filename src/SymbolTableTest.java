
import code.Code;
import code.Symbol;
import code.SymbolTable;
import java.util.Arrays;
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
        SymbolTable global = new SymbolTable();
        global.define("a");
        global.define("b");

        SymbolTable local = new SymbolTable(global);
        local.define("c");
        local.define("d");

        Symbol[] expected = {
            new Symbol("a", SymbolTable.GlobalScope, 0),
            new Symbol("b", SymbolTable.GlobalScope, 1),
            new Symbol("c", SymbolTable.LocalScope, 0),
            new Symbol("d", SymbolTable.LocalScope, 1)
        };

        for (Symbol sym : expected) {
            Symbol result = local.resolve(sym.name);
            assertNotNull("Name " + sym.name + " not resolvable", result);
            assertEquals(sym, result);
        }
    }

    @Test
    public void testDefineResolveBuiltins() {
        SymbolTable global = new SymbolTable();
        SymbolTable firstLocal = new SymbolTable(global);
        SymbolTable secondLocal = new SymbolTable(firstLocal);

        Symbol[] expected = {
            new Symbol("a", SymbolTable.BuiltinScope, 0),
            new Symbol("c", SymbolTable.BuiltinScope, 1),
            new Symbol("e", SymbolTable.BuiltinScope, 2),
            new Symbol("f", SymbolTable.BuiltinScope, 3)
        };

        for (int i = 0; i < expected.length; i++) {
            global.defineBuiltin(i, expected[i].name);
        }

        for (SymbolTable table : Arrays.asList(global, firstLocal, secondLocal)) {
            for (Symbol sym : expected) {
                Symbol result = table.resolve(sym.name);
                assertNotNull("Name " + sym.name + " not resolvable", result);
                assertEquals(sym, result);
            }
        }
    }

    @Test
    public void testResolveFree() {
        SymbolTable global = new SymbolTable();
        global.define("a");
        global.define("b");

        SymbolTable firstLocal = new SymbolTable(global);
        firstLocal.define("c");
        firstLocal.define("d");

        SymbolTable secondLocal = new SymbolTable(firstLocal);
        secondLocal.define("e");
        secondLocal.define("f");

        // Test first local scope
        Symbol[] expectedFirstLocalSymbols = {
            new Symbol("a", SymbolTable.GlobalScope, 0),
            new Symbol("b", SymbolTable.GlobalScope, 1),
            new Symbol("c", SymbolTable.LocalScope, 0),
            new Symbol("d", SymbolTable.LocalScope, 1)
        };

        for (Symbol sym : expectedFirstLocalSymbols) {
            Symbol result = firstLocal.resolve(sym.name);
            assertNotNull("Name " + sym.name + " not resolvable", result);
            assertEquals(sym, result);
        }
        assertEquals(0, firstLocal.freeSymbols.size());

        // Test second local scope
        Symbol[] expectedSecondLocalSymbols = {
            new Symbol("a", SymbolTable.GlobalScope, 0),
            new Symbol("b", SymbolTable.GlobalScope, 1),
            new Symbol("c", SymbolTable.FreeScope, 0),
            new Symbol("d", SymbolTable.FreeScope, 1),
            new Symbol("e", SymbolTable.LocalScope, 0),
            new Symbol("f", SymbolTable.LocalScope, 1)
        };

        for (Symbol sym : expectedSecondLocalSymbols) {
            Symbol result = secondLocal.resolve(sym.name);
            assertNotNull("Name " + sym.name + " not resolvable", result);
            assertEquals(sym, result);
        }

        Symbol[] expectedFreeSymbols = {
            new Symbol("c", SymbolTable.LocalScope, 0),
            new Symbol("d", SymbolTable.LocalScope, 1)
        };

        assertEquals(expectedFreeSymbols.length, secondLocal.freeSymbols.size());
        for (int i = 0; i < expectedFreeSymbols.length; i++) {
            assertEquals(expectedFreeSymbols[i], secondLocal.freeSymbols.get(i));
        }
    }

    @Test
    public void testResolveUnresolvableFree() {
        SymbolTable global = new SymbolTable();
        global.define("a");

        SymbolTable firstLocal = new SymbolTable(global);
        firstLocal.define("c");

        SymbolTable secondLocal = new SymbolTable(firstLocal);
        secondLocal.define("e");
        secondLocal.define("f");

        Symbol[] expected = {
            new Symbol("a", SymbolTable.GlobalScope, 0),
            new Symbol("c", SymbolTable.FreeScope, 0),
            new Symbol("e", SymbolTable.LocalScope, 0),
            new Symbol("f", SymbolTable.LocalScope, 1)
        };

        for (Symbol sym : expected) {
            Symbol result = secondLocal.resolve(sym.name);
            assertNotNull("Name " + sym.name + " not resolvable", result);
            assertEquals("Symbol mismatch for name " + sym.name, sym, result);
        }

        String[] expectedUnresolvable = {"b", "d"};
        for (String name : expectedUnresolvable) {
            Symbol result = secondLocal.resolve(name);
            assertNull("Name " + name + " resolved, but was expected not to", result);
        }
    }

}
