package syntactic.parseTree;

public class TreeToken {

    public enum Type { ROOT, MAIN, FUNCTION, RETURN, DECL, ASSIGN, OP, IF, ELSE, FOR, ID, SWITCH, CASE, CALL }

    private Type type;

    public Type getTokenType(){
        return type;
    }

    public TreeToken(Type type) {
        this.type = type;
    }

    public boolean isRoot() {
        return type == Type.ROOT;
    }

    public boolean isMain() {
        return type == Type.MAIN;
    }

    public boolean isFunction() {
        return type == Type.FUNCTION;
    }

    public boolean isReturn() {
        return type == Type.RETURN;
    }

    public boolean isDecl() {
        return type == Type.DECL;
    }

    public boolean isOP() {
        return type == Type.OP;
    }

    public boolean isIf() {
        return type == Type.IF;
    }

    public boolean isElse() {
        return type == Type.ELSE;
    }

    public boolean isFor() {
        return type == Type.FOR;
    }

    public boolean isID() {
        return type == Type.ID;
    }

    @Override
    public String toString() {
        return type.toString() + ": ";
    }
}
