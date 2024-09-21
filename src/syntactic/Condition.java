package syntactic;

import lexical.Token;
import syntactic.Symbol;

public class Condition {
    private Symbol a;
    private Symbol b;
    private Token.Type type;

    public Condition(Symbol a, Symbol b, Token.Type type) {
        this.a = a;
        this.b = b;
        this.type = type;
    }

    public Symbol getA() {
        return a;
    }

    public Symbol getB() {
        return b;
    }

    public Token.Type getType() {
        return type;
    }

    public Token.Type negate() {
        if (type == Token.Type.MAYOR) return Token.Type.MENOR_O_IGUAL;
        if (type == Token.Type.MENOR) return Token.Type.MAYOR_O_IGUAL;
        if (type == Token.Type. MAYOR_O_IGUAL) return Token.Type.MENOR;
        if (type == Token.Type.MENOR_O_IGUAL) return Token.Type.MAYOR;
        if (type == Token.Type.IGUAL) return Token.Type.DIF;
        if (type == Token.Type.DIF) return Token.Type.IGUAL;

        return null;
    }

    public static String conditionSymbol(Token.Type type) {
        if (type == Token.Type.MAYOR) return ">";
        if (type == Token.Type.MENOR) return "<";
        if (type == Token.Type. MAYOR_O_IGUAL) return ">=";
        if (type == Token.Type.MENOR_O_IGUAL) return "<=";
        if (type == Token.Type.IGUAL) return "==";
        if (type == Token.Type.DIF) return "!=";

        return null;
    }

    @Override
    public String toString() {
        return a.getInfo() + " " + type + " " + b.getInfo();
    }
}
