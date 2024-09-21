package syntactic.parseTree;

import syntactic.Symbol;

public class TreeTokenID extends TreeToken {
    private Symbol symbol;

    public TreeTokenID(Symbol symbol) {
        super(Type.ID);

        this.symbol = symbol;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return super.toString() + symbol.getInfo() + " (" + symbol.getType() + ")";
    }
}
