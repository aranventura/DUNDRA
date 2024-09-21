package syntactic.parseTree;

import lexical.Token;

public class TreeTokenDECL extends TreeToken {
    private String tag;
    private Token.Type type;

    public TreeTokenDECL(String tag, Token.Type type) {
        super(Type.DECL);

        this.tag = tag;
        this.type = type;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Token.Type getType() {
        return type;
    }

    public void setType(Token.Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return super.toString() + tag + ", " + type;
    }
}
