package syntactic.parseTree;

import lexical.Token;
import syntactic.Symbol;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class TreeTokenFUNCTION extends TreeToken {
    private Token.Type returnType;
    private String tag;
    private LinkedHashMap<String, Symbol> params;

    public TreeTokenFUNCTION(Token.Type returnType, String tag, LinkedHashMap<String, Symbol> params) {
        super(Type.FUNCTION);

        this.returnType = returnType;
        this.tag = tag;
        this.params = params;
    }

    public Token.Type getReturnType() {
        return returnType;
    }

    public String getTag() {
        return tag;
    }

    public HashMap<String, Symbol> getParams() {
        return params;
    }

    public void setParams(LinkedHashMap<String, Symbol> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        String[] paramInfo = new String[params.size()];

        int count = 0;
        for (String i: params.keySet()) {
            paramInfo[count++] = params.get(i) + " " + i;
        }

        return super.toString() + tag + " (" + String.join(", ", paramInfo) + ") returns " + returnType;
    }
}
