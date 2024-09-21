package syntactic.parseTree;

import syntactic.Symbol;

public class TreeTokenSWITCH extends TreeToken {
    private Symbol value;

    public TreeTokenSWITCH(Symbol value) {
        super(Type.SWITCH);

        this.value = value;
    }

    public Symbol getValue() {
        return value;
    }

    @Override
    public String toString() {
        return super.toString() + value.getInfo();
    }
}
