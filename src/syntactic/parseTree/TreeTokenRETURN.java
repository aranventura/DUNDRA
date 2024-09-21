package syntactic.parseTree;

import syntactic.Symbol;

public class TreeTokenRETURN extends TreeToken {
    private Symbol value;

    public TreeTokenRETURN(Symbol value) {
        super(Type.RETURN);

        this.value = value;
    }

    public Symbol getSymbol() {
        return value;
    }

    @Override
    public String toString() {
        return super.toString() + value.getInfo();
    }
}
