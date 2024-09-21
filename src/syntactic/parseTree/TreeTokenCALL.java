package syntactic.parseTree;

import lexical.Token;
import syntactic.Symbol;

import java.util.ArrayList;

public class TreeTokenCALL extends TreeToken {
    private Token.Type returnType;
    private String tag;
    private ArrayList<Symbol> params;

    public TreeTokenCALL(Token.Type returnType, String tag, ArrayList<Symbol> params) {
        super(Type.CALL);

        this.returnType = returnType;
        this.tag = tag;
        this.params = params;
    }

    @Override
    public String toString() {
        String[] paramInfo = new String[params.size()];

        int count = 0;
        for (Symbol i: params) {
            paramInfo[count++] = i.getInfo();
        }

        return super.toString() + tag + " (" + String.join(", ", paramInfo) + ")";
    }

    public Token.Type getReturnType() {
        return returnType;
    }

    public ArrayList<Symbol> getParams() {
        return params;
    }

    public void setParams(ArrayList<Symbol> params) {
        this.params = params;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
