package syntactic.parseTree;

public class TreeTokenASSIGN extends TreeToken {
    private String tag;

    public TreeTokenASSIGN(String tag) {
        super(Type.ASSIGN);

        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    @Override
    public String toString() {
        return super.toString() + tag;
    }
}
