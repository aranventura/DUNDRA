package syntactic.parseTree;

public class TreeTokenOP extends TreeToken {
    public enum OPType { SUMA, RESTA, MULT, DIV }
    private OPType type;

    public TreeTokenOP(OPType opType) {
        super(Type.OP);

        this.type = opType;
    }

    public boolean isSuma() {
        return type == OPType.SUMA;
    }

    public boolean isResta() {
        return type == OPType.RESTA;
    }

    public boolean isMult() {
        return type == OPType.MULT;
    }

    public boolean isDiv() {
        return type == OPType.DIV;
    }

    public String getTypeString() {
        if (isSuma()) return "+";
        if (isResta()) return "-";
        if (isMult()) return "*";
        return "/";
    }

    public OPType getType() {
        return type;
    }

    public void setType(OPType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return super.toString() + getTypeString();
    }
}
