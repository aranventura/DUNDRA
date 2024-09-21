package syntactic.parseTree;

import syntactic.Symbol;

public class TreeTokenCASE extends TreeToken {
    private Symbol value;

    public TreeTokenCASE(Symbol value) {
        super(Type.CASE);

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
