package syntactic.parseTree;

import syntactic.Symbol;

public class TreeTokenFOR extends TreeToken {
    private Symbol rounds;

    public TreeTokenFOR(Symbol rounds) {
        super(Type.FOR);

        this.rounds = rounds;
    }

    public Symbol getSymbol() {
        return rounds;
    }

    @Override
    public String toString() {
        return super.toString() + rounds.getInfo() + " iterations";
    }
}
