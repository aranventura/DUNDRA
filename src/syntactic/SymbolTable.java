package syntactic;

import java.util.LinkedHashMap;

public class SymbolTable {
    private LinkedHashMap<String, Symbol> table;

    public SymbolTable() {
        table = new LinkedHashMap<>();
    }

    public void add(String tag, Symbol symbol) {
        if (table == null) new SymbolTable();

        table.put(tag, symbol);
    }

    public Symbol get(String tag) {
        if (table == null) return null;

        if (table.containsKey(tag)) return table.get(tag);
        else return null;
    }

    public void update(String tag, Object value) {
        if (table != null && table.containsKey(tag))  table.get(tag).setValue(value);
    }

    public void update(String tag, Symbol symbol) {
        if (table != null && table.containsKey(tag))  table.get(tag).setValue(symbol.getValue());
    }

    public boolean exists(String tag) {
        if (table == null) return false;

        return table.containsKey(tag);
    }

    public LinkedHashMap<String, Symbol> getTable() {
        return table;
    }
}
